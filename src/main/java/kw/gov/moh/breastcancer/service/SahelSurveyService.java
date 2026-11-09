package kw.gov.moh.breastcancer.service;

import kw.gov.moh.breastcancer.dto.ParticipantIdentity;
import kw.gov.moh.breastcancer.dto.sahel.SahelInputItem;
import kw.gov.moh.breastcancer.dto.sahel.SahelServiceResponse;
import kw.gov.moh.breastcancer.entity.*;
import kw.gov.moh.breastcancer.entity.enums.Channel;
import kw.gov.moh.breastcancer.exception.SurveyException;
import kw.gov.moh.breastcancer.exception.SurveyMessage;
import kw.gov.moh.breastcancer.identity.IdentityProvider;
import kw.gov.moh.breastcancer.repository.CampaignRepository;
import kw.gov.moh.breastcancer.repository.SurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Sahel Service Request flow.
 *   GET  -> verify, create/resume draft, return current step.
 *   POST -> merge + prune + validate + save the submitted step, return next step or finish.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SahelSurveyService {

    private static final Set<String> CHECKED = Set.of("true", "1", "yes", "on");

    private final CampaignRepository campaignRepository;
    private final SurveyResponseRepository responseRepository;
    private final IdentityProvider identityProvider;
    private final EligibilityService eligibility;
    private final SurveyDefinitionService definitions;
    private final AnswerService answerService;
    private final SahelFormMapper formMapper;
    // private final ParticipantRefHasher hasher;
    private final AuditService audit;
    private final Clock clock;

    // =====================================================================
    // GET
    // =====================================================================
    @Transactional
    public SahelServiceResponse start(String civilId, String lang) {
        Campaign campaign = currentCampaign();
        // String ref = hasher.hash(civilId);
        ParticipantIdentity identity = verifiedIdentity(civilId, campaign);

        SurveyResponse response = responseRepository
                .findByParticipantRefAndCampaignId(civilId, campaign.getId())
                .orElse(null);

        if (response != null && response.isCompleted()) {
            throw SurveyMessage.ALREADY_SUBMITTED.ex();                       // FR-017
        }
        if (response == null) {
            response = createDraft(civilId, campaign, identity, lang);           // FR-001 checked here only
        } else {
            response.setLanguage(lang);                                      // Jira 5: keep language
        }

        SurveyDefinition def = definitions.get(response.getVersion().getId());
        Map<String, List<String>> answers = answerService.toMap(response.getAnswers());
        answerService.applyPrefill(answers, def, identity, response.getAgeAtStart());

        SurveyStep step = def.step(response.getCurrentStepNo());             // resume point
        return SahelServiceResponse.step(
                formMapper.build(step, def, response.getVersion(), answers, lang));
    }

    // =====================================================================
    // POST
    // =====================================================================
    @Transactional
    public SahelServiceResponse submitStep(String civilId, int stepNo, String lang, List<SahelInputItem> items) {
        Campaign campaign = currentCampaign();                               // FR-020: closed => no continue/submit
        // String ref = hasher.hash(civilId);

        SurveyResponse response = responseRepository.findForUpdate(civilId, campaign.getId())
                .orElseThrow(SurveyMessage.NO_DRAFT::ex);                    // GET must come first
        if (response.isCompleted()) {
            throw SurveyMessage.ALREADY_SUBMITTED.ex();                      // immutable after submit
        }

        ParticipantIdentity identity = verifiedIdentity(civilId, campaign);
        SurveyDefinition def = definitions.get(response.getVersion().getId());
        QuestionnaireVersion version = response.getVersion();

        // ---- build the full answer set ----
        Map<String, List<String>> answers = answerService.merge(
                answerService.toMap(response.getAnswers()),
                answerService.parse(items, def));
        answerService.applyPrefill(answers, def, identity, response.getAgeAtStart());
        answers = def.prune(answers);                                        // Jira 4

        // ---- out of sync (e.g. resumed session) -> show the expected step ----
        if (stepNo != response.getCurrentStepNo()) {
            log.info("Step mismatch: received {}, expected {}", stepNo, response.getCurrentStepNo());
            saveAnswers(response, answers, def, lang);
            return SahelServiceResponse.step(formMapper.build(
                    def.step(response.getCurrentStepNo()), def, version, answers, lang));
        }

        SurveyStep step = def.step(stepNo);
        answerService.validateStep(step, def, answers);                      // Jira 3

        var next = def.nextStepAfter(stepNo, answers);

        // ---- final submission ----
        if (step.isReviewStep() || next.isEmpty()) {
            if (step.isReviewStep() && !isConfirmed(items)) {
                throw SurveyMessage.CONFIRM_REQUIRED.ex();
            }
            return finalSubmit(response, answers, def, version, lang);
        }

        // ---- save draft and move on ----
        saveAnswers(response, answers, def, lang);
        response.setCurrentStepNo(next.get().getStepNo());
        return SahelServiceResponse.step(formMapper.build(next.get(), def, version, answers, lang));
    }

    // =====================================================================
    // internals
    // =====================================================================

    private SahelServiceResponse finalSubmit(SurveyResponse response, Map<String, List<String>> answers,
                                             SurveyDefinition def, QuestionnaireVersion version, String lang) {
        answerService.validateAll(def, answers);                             // Jira 6
        saveAnswers(response, answers, def, lang);
        response.complete(Channel.SAHEL);                                    // FR-022
        responseRepository.flush();                                          // fail here, not after success msg

        audit.log(AuditService.SUBMIT, response.getParticipantRef(),
                response.getCampaign().getId(), "stepNo=" + response.getCurrentStepNo());

        return SahelServiceResponse.finish(
                version.getThankYouAr(), version.getThankYouEn(),
                formMapper.finishActions(version));                          // FR-016
    }

    private SurveyResponse createDraft(String civilId, Campaign campaign, ParticipantIdentity identity, String lang) {
        int age;
        try {
            age = eligibility.checkNewParticipant(identity);
        } catch (SurveyException e) {
            audit.log(AuditService.ELIGIBILITY_FAIL, civilId, campaign.getId(), null);
            throw e;
        }

        SurveyDefinition def = definitions.get(campaign.getVersion().getId());

        SurveyResponse r = new SurveyResponse();
        r.setParticipantRef(civilId);
        r.setCampaign(campaign);
        r.setVersion(campaign.getVersion());
        r.setAgeAtStart(age);                                                // FR-004
        r.setAgeGroup(eligibility.ageGroup(age));
        r.setGovernorate(identity.governorate());
        r.setLanguage(lang);
        r.setStartChannel(Channel.SAHEL);
        r.setCurrentStepNo(def.firstStep().getStepNo());

        SurveyResponse saved = responseRepository.saveAndFlush(r);           // unique key guards races
        audit.log(AuditService.DRAFT_CREATED, civilId, campaign.getId(), null);
        return saved;
    }

    private void saveAnswers(SurveyResponse response, Map<String, List<String>> answers,
                             SurveyDefinition def, String lang) {
        response.replaceAnswers(answerService.toEntities(answers, def));     // orphanRemoval deletes old rows
        response.setLanguage(lang);
    }

    private Campaign currentCampaign() {
        return campaignRepository.findCurrentAvailable(LocalDateTime.now(clock))
                .orElseThrow(SurveyMessage.NO_ACTIVE_CAMPAIGN::ex);
    }

    private ParticipantIdentity verifiedIdentity(String civilId, Campaign campaign) {
        ParticipantIdentity identity = identityProvider.findByCivilId(civilId).orElse(null);
        try {
            eligibility.requireVerifiedIdentity(identity);                   // FR-003
        } catch (SurveyException e) {
            audit.log(AuditService.IDENTITY_FAIL, civilId, campaign.getId(), null);
            throw e;
        }
        return identity;
    }

    private boolean isConfirmed(List<SahelInputItem> items) {
        return items != null && items.stream().anyMatch(i ->
                SahelFormMapper.CONFIRM_KEY.equalsIgnoreCase(i.getKey())
                        && i.getValue() != null
                        && CHECKED.contains(i.getValue().trim().toLowerCase()));
    }
}
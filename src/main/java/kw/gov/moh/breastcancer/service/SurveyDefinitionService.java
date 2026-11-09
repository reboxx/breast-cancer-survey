package kw.gov.moh.breastcancer.service;

import kw.gov.moh.breastcancer.config.SurveyProperties;
import kw.gov.moh.breastcancer.repository.QuestionConditionRepository;
import kw.gov.moh.breastcancer.repository.QuestionRepository;
import kw.gov.moh.breastcancer.repository.SurveyStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Loads a questionnaire version once and caches it (definition changes only via a new approved version). */
@Service
@RequiredArgsConstructor
public class SurveyDefinitionService {

    private final SurveyStepRepository stepRepository;
    private final QuestionRepository questionRepository;
    private final QuestionConditionRepository conditionRepository;
    private final SurveyProperties props;

    private final Map<Long, SurveyDefinition> cache = new ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    public SurveyDefinition get(Long versionId) {
        SurveyDefinition def = cache.get(versionId);
        if (def == null) {
            def = load(versionId);
            cache.put(versionId, def);
        }
        return def;
    }

    /** Call after loading/approving a new version in DB. */
    public void evict(Long versionId) {
        cache.remove(versionId);
    }

    private SurveyDefinition load(Long versionId) {
        var steps = stepRepository.findByVersionIdOrderByStepNoAsc(versionId);
        if (steps.isEmpty()) {
            throw new IllegalStateException("Questionnaire version " + versionId + " has no steps");
        }
        return new SurveyDefinition(
                versionId,
                steps,
                questionRepository.findAllByVersionWithOptions(versionId),
                conditionRepository.findAllByVersion(versionId),
                props.multiValueSeparator());
    }
}
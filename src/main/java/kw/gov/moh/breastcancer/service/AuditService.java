package kw.gov.moh.breastcancer.service;

import kw.gov.moh.breastcancer.entity.AuditLog;
import kw.gov.moh.breastcancer.entity.enums.Channel;
import kw.gov.moh.breastcancer.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** NFR-04. Own transaction so the audit survives a rollback of the caller. Never pass answer content. */
@Service
@RequiredArgsConstructor
public class AuditService {

    public static final String SUBMIT = "SUBMIT";
    public static final String DRAFT_CREATED = "DRAFT_CREATED";
    public static final String ELIGIBILITY_FAIL = "ELIGIBILITY_FAIL";
    public static final String IDENTITY_FAIL = "IDENTITY_FAIL";

    private final AuditLogRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String eventType, String participantRef, Long campaignId, String details) {
        AuditLog a = new AuditLog();
        a.setEventType(eventType);
        a.setParticipantRef(participantRef);
        a.setCampaignId(campaignId);
        a.setChannel(Channel.SAHEL);
        a.setActor("SAHEL");
        a.setDetails(details);
        repository.save(a);
    }
}
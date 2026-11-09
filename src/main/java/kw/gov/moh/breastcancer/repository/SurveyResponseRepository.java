package kw.gov.moh.breastcancer.repository;

import jakarta.persistence.LockModeType;
import kw.gov.moh.breastcancer.entity.SurveyResponse;
import kw.gov.moh.breastcancer.entity.enums.Channel;
import kw.gov.moh.breastcancer.entity.enums.ResponseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    Optional<SurveyResponse> findByParticipantRefAndCampaignId(String participantRef, Long campaignId);

    /** Row lock for POST/final submit – prevents two parallel submissions (FR-021). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           select r from SurveyResponse r
           where r.participantRef = :ref and r.campaign.id = :campaignId
           """)
    Optional<SurveyResponse> findForUpdate(@Param("ref") String participantRef,
                                           @Param("campaignId") Long campaignId);

    boolean existsByParticipantRefAndCampaignIdAndStatus(String participantRef, Long campaignId,
                                                         ResponseStatus status);

    // ---- participation statistics (FR-025) ----
    long countByCampaignId(Long campaignId);

    long countByCampaignIdAndStatus(Long campaignId, ResponseStatus status);

    long countByCampaignIdAndStatusAndSubmissionChannel(Long campaignId, ResponseStatus status,
                                                        Channel channel);
}
package kw.gov.moh.breastcancer.repository;

import kw.gov.moh.breastcancer.entity.SurveyStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SurveyStepRepository extends JpaRepository<SurveyStep, Long> {

    List<SurveyStep> findByVersionIdOrderByStepNoAsc(Long versionId);

    Optional<SurveyStep> findByVersionIdAndStepNo(Long versionId, Integer stepNo);

    Optional<SurveyStep> findByVersionIdAndIntroStepTrue(Long versionId);

    Optional<SurveyStep> findByVersionIdAndReviewStepTrue(Long versionId);

    @Query("select max(s.stepNo) from SurveyStep s where s.version.id = :versionId")
    Integer findLastStepNo(@Param("versionId") Long versionId);
}
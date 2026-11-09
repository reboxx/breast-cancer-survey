package kw.gov.moh.breastcancer.repository;

import kw.gov.moh.breastcancer.entity.QuestionnaireVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionnaireVersionRepository extends JpaRepository<QuestionnaireVersion, Long> {

    Optional<QuestionnaireVersion> findFirstByActiveTrue();

    Optional<QuestionnaireVersion> findByVersionNo(String versionNo);
}
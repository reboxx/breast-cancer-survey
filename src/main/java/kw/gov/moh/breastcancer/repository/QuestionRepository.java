package kw.gov.moh.breastcancer.repository;

import kw.gov.moh.breastcancer.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    /** All questions of a version with their step and options in one query (cache this). */
    @Query("""
           select distinct q from Question q
           join fetch q.step s
           left join fetch q.options
           where s.version.id = :versionId
           order by s.stepNo, q.displayOrder
           """)
    List<Question> findAllByVersionWithOptions(@Param("versionId") Long versionId);

    /** Questions of one step with options. */
    @Query("""
           select distinct q from Question q
           left join fetch q.options
           where q.step.id = :stepId
           order by q.displayOrder
           """)
    List<Question> findByStepWithOptions(@Param("stepId") Long stepId);
}
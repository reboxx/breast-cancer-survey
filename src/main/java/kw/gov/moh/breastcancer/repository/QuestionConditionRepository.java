package kw.gov.moh.breastcancer.repository;

import kw.gov.moh.breastcancer.entity.QuestionCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionConditionRepository extends JpaRepository<QuestionCondition, Long> {

    /** All branching rules of a version (question- and step-level). */
    @Query("""
           select c from QuestionCondition c
           join fetch c.parentQuestion pq
           left join fetch c.question
           left join fetch c.step
           where pq.step.version.id = :versionId
           """)
    List<QuestionCondition> findAllByVersion(@Param("versionId") Long versionId);
}
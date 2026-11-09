package kw.gov.moh.breastcancer.repository;

import kw.gov.moh.breastcancer.entity.ResponseAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResponseAnswerRepository extends JpaRepository<ResponseAnswer, Long> {

    /** Saved answers for resume / prefill (DefaultValue). */
    @Query("""
           select a from ResponseAnswer a
           join fetch a.question q
           where a.response.id = :responseId
           """)
    List<ResponseAnswer> findByResponseIdWithQuestion(@Param("responseId") Long responseId);

    /** Bulk delete before re-inserting the full answer set from a POST. */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from ResponseAnswer a where a.response.id = :responseId")
    int deleteByResponseId(@Param("responseId") Long responseId);
}
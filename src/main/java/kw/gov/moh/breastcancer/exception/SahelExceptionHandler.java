package kw.gov.moh.breastcancer.exception;

import kw.gov.moh.breastcancer.controller.SahelSurveyController;
import kw.gov.moh.breastcancer.dto.sahel.SahelServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Sahel expects JSON with IsSuccess=false, so every error returns HTTP 200. */
@Slf4j
@RestControllerAdvice(assignableTypes = SahelSurveyController.class)
public class SahelExceptionHandler {

    @ExceptionHandler(SurveyException.class)
    public SahelServiceResponse business(SurveyException e) {
        return SahelServiceResponse.fail(e.getMessageAr(), e.getMessageEn());
    }

    /** Parallel request on the same participant (double tap / race on draft creation). */
    @ExceptionHandler({DataIntegrityViolationException.class, ObjectOptimisticLockingFailureException.class})
    public SahelServiceResponse concurrency(RuntimeException e) {
        log.warn("Concurrent survey request rejected: {}", e.getClass().getSimpleName());
        return fail(SurveyMessage.GENERAL_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public SahelServiceResponse unexpected(Exception e) {
        log.error("Unexpected survey error", e);   // never log request bodies (health data)
        return fail(SurveyMessage.GENERAL_ERROR);
    }

    private SahelServiceResponse fail(SurveyMessage m) {
        return SahelServiceResponse.fail(m.ar(), m.en());
    }
}
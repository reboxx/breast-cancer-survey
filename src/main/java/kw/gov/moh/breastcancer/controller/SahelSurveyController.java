package kw.gov.moh.breastcancer.controller;

import kw.gov.moh.breastcancer.dto.sahel.SahelInputItem;
import kw.gov.moh.breastcancer.dto.sahel.SahelServiceResponse;
import kw.gov.moh.breastcancer.exception.SurveyMessage;
import kw.gov.moh.breastcancer.service.SahelSurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Sahel Service Request endpoint (Sahel doc §4). One URL, GET + POST.
 * Sahel backend appends civilId (and lang / stepNo) as query parameters.
 */
@RestController
@RequestMapping(value = "/api/v1/sahel", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SahelSurveyController {

    private static final Pattern CIVIL_ID = Pattern.compile("^[1-3]\\d{11}$");

    private final SahelSurveyService service;

    /** §4.1 – build the form (called once when the user opens the service). */
    @GetMapping
    public SahelServiceResponse get(@RequestParam("civilId") String civilId,
                                    @RequestParam(value = "lang", required = false) String lang) {
        return service.start(validCivilId(civilId), normalizeLang(lang));
    }

    /** §4.3 – receive a submitted step (called for every step). */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public SahelServiceResponse post(@RequestParam("civilId") String civilId,
                                     @RequestParam("stepNo") Integer stepNo,
                                     @RequestParam(value = "lang", required = false) String lang,
                                     @RequestBody(required = false) List<SahelInputItem> items) {
        return service.submitStep(validCivilId(civilId), stepNo, normalizeLang(lang),
                items == null ? List.of() : items);
    }

    private static String validCivilId(String civilId) {
        String id = civilId == null ? "" : civilId.trim();
        if (!CIVIL_ID.matcher(id).matches()) throw SurveyMessage.INVALID_CIVIL_ID.ex();
        return id;
    }

    private static String normalizeLang(String lang) {
        return "en".equalsIgnoreCase(lang) ? "en" : "ar";
    }
}
package kw.gov.moh.breastcancer.service;

import kw.gov.moh.breastcancer.dto.ParticipantIdentity;
import kw.gov.moh.breastcancer.dto.sahel.SahelInputItem;
import kw.gov.moh.breastcancer.entity.*;
import kw.gov.moh.breastcancer.entity.enums.ControlType;
import kw.gov.moh.breastcancer.exception.SurveyException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static kw.gov.moh.breastcancer.service.SurveyDefinition.*;

/** Parsing, merging, prefill, validation and persistence mapping of answers (Jira 3, 4, 5, 8). */
@Service
public class AnswerService {

    // ---------- load / store ----------

    public Map<String, List<String>> toMap(Collection<ResponseAnswer> stored) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        for (ResponseAnswer a : stored) {
            String v = a.getOptionValue() != null ? a.getOptionValue() : a.getTextValue();
            map.computeIfAbsent(a.getQuestion().getCode(), k -> new ArrayList<>()).add(v);
        }
        return map;
    }

    public List<ResponseAnswer> toEntities(Map<String, List<String>> answers, SurveyDefinition def) {
        List<ResponseAnswer> list = new ArrayList<>();
        answers.forEach((code, values) -> def.question(code).ifPresent(q -> {
            if (isOptionControl(q)) {
                values.forEach(v -> list.add(ResponseAnswer.ofOption(q, v)));
            } else if (!values.isEmpty()) {
                list.add(ResponseAnswer.ofText(q, values.get(0)));
            }
        }));
        return list;
    }

    // ---------- incoming POST ----------

    /**
     * Converts Sahel items to answers. Keys not in the questionnaire (e.g. CONFIRM) and
     * read-only prefilled questions are ignored – their values are never trusted from the client.
     * A key present with an empty value means "cleared".
     */
    public Map<String, List<String>> parse(List<SahelInputItem> items, SurveyDefinition def) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        if (items == null) return map;
        for (SahelInputItem item : items) {
            if (item == null || item.getKey() == null) continue;
            def.question(item.getKey().trim())
                    .filter(q -> !q.isDisabled() && q.getPrefillSource() == null && !isInputLess(q))
                    .ifPresent(q -> map.put(q.getCode(), splitValue(q, item.getValue(), def.separator())));
        }
        return map;
    }

    /** Saved answers + incoming (incoming wins per question; empty incoming removes). */
    public Map<String, List<String>> merge(Map<String, List<String>> saved, Map<String, List<String>> incoming) {
        Map<String, List<String>> merged = new LinkedHashMap<>(saved);
        incoming.forEach((code, values) -> {
            if (values.isEmpty()) merged.remove(code); else merged.put(code, values);
        });
        return merged;
    }

    /** Applies read-only identity values ("available on Sahel" questions). */
    public void applyPrefill(Map<String, List<String>> answers, SurveyDefinition def,
                             ParticipantIdentity identity, int ageAtStart) {
        for (SurveyStep s : def.steps()) {
            for (Question q : def.questions(s)) {
                String v = prefillValue(q, identity, ageAtStart);
                if (v != null) answers.put(q.getCode(), List.of(v));
            }
        }
    }

    public String prefillValue(Question q, ParticipantIdentity identity, int ageAtStart) {
        if (q.getPrefillSource() == null) return null;
        return switch (q.getPrefillSource()) {
            case "SAHEL_AGE"            -> String.valueOf(ageAtStart);
            case "SAHEL_NATIONALITY"    -> "1";   // eligible participants are always Kuwaiti (option 1)
            case "SAHEL_GOVERNORATE"    -> identity.governorate();
            case "SAHEL_MARITAL_STATUS" -> identity.maritalStatus();
            case "SAHEL_HAS_CHILDREN"   -> identity.hasChildren() == null ? null
                                           : (identity.hasChildren() ? "1" : "2");
            default -> null;
        };
    }

    // ---------- validation ----------

    public void validateStep(SurveyStep step, SurveyDefinition def, Map<String, List<String>> answers) {
        for (Question q : def.visibleQuestions(step, answers)) {
            validateQuestion(q, answers.getOrDefault(q.getCode(), List.of()));
        }
    }

    /** Final submission: re-validate every applicable question (Jira 6). */
    public void validateAll(SurveyDefinition def, Map<String, List<String>> answers) {
        for (SurveyStep s : def.steps()) {
            if (def.isStepVisible(s, answers)) validateStep(s, def, answers);
        }
    }

    private void validateQuestion(Question q, List<String> values) {
        if (isInputLess(q)) return;

        if (values.isEmpty()) {
            if (q.isRequired()) throw error(q, "يرجى الإجابة على: ", "Please answer: ");
            return;
        }

        if (isOptionControl(q)) {
            Set<String> allowed = q.getOptions().stream().map(QuestionOption::getValue).collect(Collectors.toSet());
            if (!isMultiSelect(q) && values.size() > 1) throw error(q, "يرجى اختيار إجابة واحدة: ", "Select one answer: ");
            if (!allowed.containsAll(values)) throw error(q, "إجابة غير صحيحة: ", "Invalid answer: ");
            return;
        }

        String text = values.get(0);
        if (q.getMaxLength() != null && text.length() > q.getMaxLength()) {
            throw error(q, "تجاوزت الحد المسموح من الأحرف: ", "Too many characters: ");
        }
        if (q.getValidationRegex() != null && !Pattern.compile(q.getValidationRegex()).matcher(text).matches()) {
            throw new SurveyException(
                    Objects.requireNonNullElse(q.getValidationMsgAr(), "قيمة غير صحيحة: " + q.getLabelAr()),
                    Objects.requireNonNullElse(q.getValidationMsgEn(), "Invalid value: " + q.getLabelEn()));
        }
    }

    // ---------- helpers ----------

    private List<String> splitValue(Question q, String raw, String separator) {
        if (raw == null || raw.isBlank()) return List.of();
        if (isMultiSelect(q)) {
            return Arrays.stream(raw.split(Pattern.quote(separator)))
                    .map(String::trim).filter(v -> !v.isEmpty()).distinct().toList();
        }
        if (q.getControlType() == ControlType.Text) return List.of(raw.strip());
        return List.of(raw.trim());
    }

    private SurveyException error(Question q, String prefixAr, String prefixEn) {
        return new SurveyException(prefixAr + q.getLabelAr(), prefixEn + q.getLabelEn());
    }
}
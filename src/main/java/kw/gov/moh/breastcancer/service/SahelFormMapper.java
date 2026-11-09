package kw.gov.moh.breastcancer.service;

import kw.gov.moh.breastcancer.config.SurveyProperties;
import kw.gov.moh.breastcancer.dto.sahel.*;
import kw.gov.moh.breastcancer.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

import static kw.gov.moh.breastcancer.service.SurveyDefinition.*;

/** Builds Sahel form models from the questionnaire definition (Jira 2, 3, 5, 6). */
@Component
@RequiredArgsConstructor
public class SahelFormMapper {

    public static final String CONFIRM_KEY = "CONFIRM";
    private static final String REVIEW_KEY = "REVIEW_SUMMARY";

    private final SurveyProperties props;

    public SahelFormResult build(SurveyStep step, SurveyDefinition def, QuestionnaireVersion version,
                                 Map<String, List<String>> answers, String lang) {
        SahelFormResult form = SahelFormResult.builder()
                .nameAr(step.getTitleAr())
                .nameEn(step.getTitleEn())
                .hintAr(nz(step.getHintAr()))
                .hintEn(nz(step.getHintEn()))
                .stepNo(step.getStepNo())
                .buttonLabelAr(step.getButtonLabelAr())
                .buttonLabelEn(step.getButtonLabelEn())
                .build();

        if (step.isIntroStep()) {
            // Jira 2: approved introduction before the first question
            form.setHintAr(nz(version.getIntroTextAr()));
            form.setHintEn(nz(version.getIntroTextEn()));
        } else if (step.isReviewStep()) {
            form.getControlsModels().add(reviewSummary(def, answers, lang));
            form.getControlsModels().add(confirmCheckbox());
        }

        for (Question q : def.visibleQuestions(step, answers)) {
            form.getControlsModels().add(control(q, answers.get(q.getCode()), def.separator()));
        }
        return form;
    }

    public List<SahelAction> finishActions(QuestionnaireVersion version) {
        if (version.getInfoLinkUrl() == null) return null;
        return List.of(SahelAction.builder()
                .actionType(props.infoActionType())
                .actionUrl(version.getInfoLinkUrl())
                .viewBag("")
                .labelAr("لمزيد من المعلومات")
                .labelEn("More information")
                .build());
    }

    // ---------- controls ----------

    private SahelControlModel control(Question q, List<String> saved, String separator) {
        boolean readOnly = q.isDisabled() || q.getPrefillSource() != null;
        return SahelControlModel.builder()
                .id(q.getCode())
                .type(q.getControlType().name())
                .labelAr(q.getLabelAr())
                .labelEn(q.getLabelEn())
                .hintAr(nz(q.getHintAr()))
                .hintEn(nz(q.getHintEn()))
                .maxLength(q.getMaxLength() == null ? null : String.valueOf(q.getMaxLength()))
                .required(q.isRequired() && !readOnly)
                .disabled(readOnly)
                .multiline(q.isMultiline())
                .defaultValue(saved == null ? "" : String.join(separator, saved))   // resume (Jira 5)
                .optionControlModels(isOptionControl(q) ? options(q) : null)
                .validators(q.getValidationRegex() == null ? null : List.of(new SahelValidator(
                        q.getValidationRegex(), nz(q.getValidationMsgAr()), nz(q.getValidationMsgEn()))))
                .build();
    }

    private List<SahelOption> options(Question q) {
        return q.getOptions().stream()
                .sorted(Comparator.comparing(QuestionOption::getDisplayOrder))
                .map(o -> new SahelOption(o.getValue(), o.getTextAr(), o.getTextEn()))
                .toList();
    }

    /** Read-only summary of given answers for final confirmation (Jira 6). */
    private SahelControlModel reviewSummary(SurveyDefinition def, Map<String, List<String>> answers, String lang) {
        List<SahelDataTableItem> rows = new ArrayList<>();
        for (SurveyStep s : def.steps()) {
            for (Question q : def.questions(s)) {
                List<String> values = answers.get(q.getCode());
                if (values == null || values.isEmpty()) continue;
                rows.add(new SahelDataTableItem(q.getCode(), q.getLabelAr(), q.getLabelEn(),
                        displayValue(q, values, lang)));
            }
        }
        return SahelControlModel.builder()
                .id(REVIEW_KEY).type("DataTable")
                .labelAr("ملخص الإجابات").labelEn("Summary of answers")
                .required(false)
                .dataTableList(rows)
                .build();
    }

    private SahelControlModel confirmCheckbox() {
        return SahelControlModel.builder()
                .id(CONFIRM_KEY).type("Checkbox")
                .labelAr("أؤكد صحة إجاباتي وأرغب في إرسال الاستبيان")
                .labelEn("I confirm my answers and want to submit the survey")
                .required(true)
                .defaultValue("")
                .build();
    }

    private String displayValue(Question q, List<String> values, String lang) {
        if (!isOptionControl(q)) return values.get(0);
        Map<String, QuestionOption> byValue = new HashMap<>();
        q.getOptions().forEach(o -> byValue.put(o.getValue(), o));
        return values.stream()
                .map(v -> Optional.ofNullable(byValue.get(v))
                        .map(o -> "en".equals(lang) ? o.getTextEn() : o.getTextAr())
                        .orElse(v))
                .reduce((a, b) -> a + "، " + b).orElse("");
    }

    private static String nz(String s) { return s == null ? "" : s; }
}
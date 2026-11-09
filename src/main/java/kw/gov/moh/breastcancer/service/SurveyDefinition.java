package kw.gov.moh.breastcancer.service;

import kw.gov.moh.breastcancer.entity.Question;
import kw.gov.moh.breastcancer.entity.QuestionCondition;
import kw.gov.moh.breastcancer.entity.SurveyStep;
import kw.gov.moh.breastcancer.entity.enums.ControlType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable, cached view of one questionnaire version + branching engine (Jira 2, 4).
 * Question codes must be unique within a version.
 * Answers are represented as Map<questionCode, List<value>>.
 */
public class SurveyDefinition {

    private final Long versionId;
    private final List<SurveyStep> steps;                          // ordered by stepNo
    private final Map<Integer, SurveyStep> stepsByNo;
    private final Map<Long, List<Question>> questionsByStep;       // ordered by displayOrder
    private final Map<String, Question> questionsByCode;
    private final Map<Long, List<QuestionCondition>> conditionsByQuestion;
    private final Map<Long, List<QuestionCondition>> conditionsByStep;
    private final String separator;

    public SurveyDefinition(Long versionId, List<SurveyStep> steps, List<Question> questions,
                            List<QuestionCondition> conditions, String separator) {
        this.versionId = versionId;
        this.separator = separator;
        this.steps = List.copyOf(steps);
        this.stepsByNo = steps.stream()
                .collect(Collectors.toMap(SurveyStep::getStepNo, s -> s));
        this.questionsByStep = questions.stream()
                .collect(Collectors.groupingBy(q -> q.getStep().getId(), LinkedHashMap::new, Collectors.toList()));
        this.questionsByCode = questions.stream()
                .collect(Collectors.toMap(Question::getCode, q -> q, (a, b) -> {
                    throw new IllegalStateException("Duplicate question code in version: " + a.getCode());
                }));
        this.conditionsByQuestion = conditions.stream().filter(c -> c.getQuestion() != null)
                .collect(Collectors.groupingBy(c -> c.getQuestion().getId()));
        this.conditionsByStep = conditions.stream().filter(c -> c.getStep() != null)
                .collect(Collectors.groupingBy(c -> c.getStep().getId()));
    }

    // ---------- lookups ----------

    public Long versionId() { return versionId; }

    public SurveyStep firstStep() { return steps.get(0); }

    public SurveyStep step(int stepNo) {
        SurveyStep s = stepsByNo.get(stepNo);
        if (s == null) throw new IllegalStateException("Unknown step " + stepNo);
        return s;
    }

    public List<SurveyStep> steps() { return steps; }

    public List<Question> questions(SurveyStep step) {
        return questionsByStep.getOrDefault(step.getId(), List.of());
    }

    public Optional<Question> question(String code) {
        return Optional.ofNullable(questionsByCode.get(code));
    }

    public String separator() { return separator; }

    // ---------- branching ----------

    public boolean isQuestionVisible(Question q, Map<String, List<String>> answers) {
        return stepConditionsMatch(q.getStep(), answers)
                && allMatch(conditionsByQuestion.get(q.getId()), answers);
    }

    /** Step shown if its own conditions match and it has something to show. */
    public boolean isStepVisible(SurveyStep step, Map<String, List<String>> answers) {
        if (!stepConditionsMatch(step, answers)) return false;
        if (step.isIntroStep() || step.isReviewStep()) return true;
        return visibleQuestions(step, answers).stream().anyMatch(q -> !isInputLess(q));
    }

    public List<Question> visibleQuestions(SurveyStep step, Map<String, List<String>> answers) {
        return questions(step).stream().filter(q -> isQuestionVisible(q, answers)).toList();
    }

    /** Next applicable step after stepNo (skips steps whose conditions fail). */
    public Optional<SurveyStep> nextStepAfter(int stepNo, Map<String, List<String>> answers) {
        return steps.stream()
                .filter(s -> s.getStepNo() > stepNo)
                .filter(s -> isStepVisible(s, answers))
                .findFirst();
    }

    /**
     * Jira 4: drop answers of unknown or no-longer-applicable questions.
     * Evaluated in questionnaire order so chained conditions resolve correctly.
     */
    public Map<String, List<String>> prune(Map<String, List<String>> answers) {
        Map<String, List<String>> result = new LinkedHashMap<>(answers);
        result.keySet().removeIf(code -> !questionsByCode.containsKey(code));
        for (SurveyStep s : steps) {
            for (Question q : questions(s)) {
                if (result.containsKey(q.getCode()) && !isQuestionVisible(q, result)) {
                    result.remove(q.getCode());
                }
            }
        }
        return result;
    }

    // ---------- helpers ----------

    public static boolean isOptionControl(Question q) {
        return q.getControlType() == ControlType.Radio
                || q.getControlType() == ControlType.Dropdown
                || q.getControlType() == ControlType.CheckBoxList;
    }

    public static boolean isMultiSelect(Question q) {
        return q.getControlType() == ControlType.CheckBoxList;
    }

    public static boolean isInputLess(Question q) {
        return q.getControlType() == ControlType.DataTable;
    }

    private boolean stepConditionsMatch(SurveyStep step, Map<String, List<String>> answers) {
        return allMatch(conditionsByStep.get(step.getId()), answers);
    }

    /** Multiple conditions on the same target are AND-ed. */
    private boolean allMatch(List<QuestionCondition> conditions, Map<String, List<String>> answers) {
        if (conditions == null || conditions.isEmpty()) return true;
        return conditions.stream().allMatch(c -> matches(c, answers));
    }

    private boolean matches(QuestionCondition c, Map<String, List<String>> answers) {
        List<String> actual = answers.getOrDefault(c.getParentQuestion().getCode(), List.of());
        Set<String> expected = Arrays.stream(c.getExpectedValue().split(","))
                .map(String::trim).filter(v -> !v.isEmpty()).collect(Collectors.toSet());
        return switch (c.getOperator()) {
            case EQUALS     -> actual.size() == 1 && expected.contains(actual.get(0));
            case NOT_EQUALS -> !actual.isEmpty() && actual.stream().noneMatch(expected::contains);
            case IN         -> actual.stream().anyMatch(expected::contains);
            case CONTAINS   -> actual.containsAll(expected);
        };
    }
}
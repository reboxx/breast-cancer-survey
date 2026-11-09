package kw.gov.moh.breastcancer.entity;


import jakarta.persistence.*;
import kw.gov.moh.breastcancer.entity.enums.ConditionOperator;
import lombok.*;

@Entity
@Table(name = "breast_cancer_question_condition")
@Getter @Setter @NoArgsConstructor
public class QuestionCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "condition_id")
    private Long id;

    /** Dependent question (exactly one of question / step is set). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    /** Dependent whole step. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id")
    private SurveyStep step;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_question_id", nullable = false)
    private Question parentQuestion;

    @Enumerated(EnumType.STRING)
    @Column(name = "operator", nullable = false, length = 20)
    private ConditionOperator operator;

    /** e.g. "1" or "1,3" for IN. */
    @Column(name = "expected_value", nullable = false, length = 200)
    private String expectedValue;
}
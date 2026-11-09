package kw.gov.moh.breastcancer.entity;

import jakarta.persistence.*;
import kw.gov.moh.breastcancer.entity.enums.ControlType;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "breast_cancer_question",
       uniqueConstraints = @UniqueConstraint(columnNames = {"step_id", "code"}))
@Getter @Setter @NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "step_id", nullable = false)
    private SurveyStep step;

    /** Sent to Sahel as control Id, received back as POST Key. */
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "label_ar", nullable = false, length = 1000)
    private String labelAr;

    @Column(name = "label_en", nullable = false, length = 1000)
    private String labelEn;

    @Column(name = "hint_ar", length = 500)
    private String hintAr;

    @Column(name = "hint_en", length = 500)
    private String hintEn;

    @Enumerated(EnumType.STRING)
    @Column(name = "control_type", nullable = false, length = 30)
    private ControlType controlType;

    @Column(name = "is_required", nullable = false)
    private boolean required = true;

    @Column(name = "is_disabled", nullable = false)
    private boolean disabled;

    @Column(name = "is_multiline", nullable = false)
    private boolean multiline;

    @Column(name = "max_length")
    private Integer maxLength;

    @Column(name = "validation_regex", length = 500)
    private String validationRegex;

    @Column(name = "validation_msg_ar", length = 200)
    private String validationMsgAr;

    @Column(name = "validation_msg_en", length = 200)
    private String validationMsgEn;

    /** e.g. SAHEL_AGE, SAHEL_NATIONALITY – value prefilled read-only from identity data. */
    @Column(name = "prefill_source", length = 50)
    private String prefillSource;

    @Column(name = "is_sensitive", nullable = false)
    private boolean sensitive;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    private List<QuestionOption> options = new ArrayList<>();

    /** Conditions controlling THIS question's visibility. */
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<QuestionCondition> conditions = new ArrayList<>();
}
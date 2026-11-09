package kw.gov.moh.breastcancer.entity;


import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "breast_cancer_survey_step",
       uniqueConstraints = @UniqueConstraint(columnNames = {"version_id", "step_no"}))
@Getter @Setter @NoArgsConstructor
public class SurveyStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "version_id", nullable = false)
    private QuestionnaireVersion version;

    @Column(name = "step_no", nullable = false)
    private Integer stepNo;

    @Column(name = "section_code", nullable = false, length = 50)
    private String sectionCode;

    @Column(name = "title_ar", nullable = false, length = 200)
    private String titleAr;

    @Column(name = "title_en", nullable = false, length = 200)
    private String titleEn;

    @Column(name = "hint_ar", length = 500)
    private String hintAr;

    @Column(name = "hint_en", length = 500)
    private String hintEn;

    @Column(name = "button_label_ar", length = 50)
    private String buttonLabelAr;

    @Column(name = "button_label_en", length = 50)
    private String buttonLabelEn;

    @Column(name = "is_intro_step", nullable = false)
    private boolean introStep;

    @Column(name = "is_review_step", nullable = false)
    private boolean reviewStep;

    @OneToMany(mappedBy = "step", fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    private List<Question> questions = new ArrayList<>();

    /** Step-level conditions (whole step shown/skipped). */
    @OneToMany(mappedBy = "step", fetch = FetchType.LAZY)
    private List<QuestionCondition> conditions = new ArrayList<>();
}
package kw.gov.moh.breastcancer.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "breast_cancer_question_option",
       uniqueConstraints = @UniqueConstraint(columnNames = {"question_id", "value"}))
@Getter @Setter @NoArgsConstructor
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "value", nullable = false, length = 50)
    private String value;

    @Column(name = "text_ar", nullable = false, length = 500)
    private String textAr;

    @Column(name = "text_en", nullable = false, length = 500)
    private String textEn;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "is_other", nullable = false)
    private boolean other;
}
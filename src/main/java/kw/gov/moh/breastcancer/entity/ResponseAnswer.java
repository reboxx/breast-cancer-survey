package kw.gov.moh.breastcancer.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "breast_cancer_response_answer")
@Getter @Setter @NoArgsConstructor
public class ResponseAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "response_id", nullable = false)
    private SurveyResponse response;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /** One row per selected option (multi-select = several rows). */
    @Column(name = "option_value", length = 50)
    private String optionValue;

    @Column(name = "text_value", columnDefinition = "nvarchar(max)")
    private String textValue;

    @Column(name = "answered_at", nullable = false)
    private LocalDateTime answeredAt;

    @PrePersist
    void onCreate() { if (answeredAt == null) answeredAt = LocalDateTime.now(); }

    public static ResponseAnswer ofOption(Question q, String value) {
        ResponseAnswer a = new ResponseAnswer();
        a.setQuestion(q);
        a.setOptionValue(value);
        return a;
    }

    public static ResponseAnswer ofText(Question q, String text) {
        ResponseAnswer a = new ResponseAnswer();
        a.setQuestion(q);
        a.setTextValue(text);
        return a;
    }
}
package kw.gov.moh.breastcancer.entity;


import jakarta.persistence.*;
import kw.gov.moh.breastcancer.entity.enums.Channel;
import kw.gov.moh.breastcancer.entity.enums.ResponseStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "breast_cancer_survey_response",
       uniqueConstraints = @UniqueConstraint(columnNames = {"participant_ref", "campaign_id"}))
@Getter @Setter @NoArgsConstructor
public class SurveyResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long id;

    /** SHA-256 hex of civil ID + secret salt – never the raw civil ID. */
    @Column(name = "participant_ref", nullable = false, columnDefinition = "char(64)")
    private String participantRef;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "version_id", nullable = false)
    private QuestionnaireVersion version;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ResponseStatus status = ResponseStatus.DRAFT;

    @Column(name = "current_step_no", nullable = false)
    private Integer currentStepNo = 1;

    @Column(name = "language", nullable = false, columnDefinition = "char(2)")
    private String language = "ar";

    @Column(name = "age_at_start", nullable = false)
    private Integer ageAtStart;

    @Column(name = "age_group", length = 10)
    private String ageGroup;

    @Column(name = "governorate", length = 50)
    private String governorate;

    @Enumerated(EnumType.STRING)
    @Column(name = "start_channel", nullable = false, length = 10)
    private Channel startChannel;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_channel", length = 10)
    private Channel submissionChannel;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    /** SQL Server ROWVERSION – optimistic locking against double submit. */
    @Version
    @Column(name = "row_ver", insertable = false, updatable = false, columnDefinition = "rowversion")
    private byte[] rowVer;

    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ResponseAnswer> answers = new ArrayList<>();

    @PrePersist
    void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }

    public boolean isCompleted() { return status == ResponseStatus.COMPLETED; }

    /** Replace all draft answers (Sahel resends everything each POST). */
    public void replaceAnswers(List<ResponseAnswer> newAnswers) {
        answers.clear();
        newAnswers.forEach(a -> { a.setResponse(this); answers.add(a); });
    }

    public void complete(Channel channel) {
        status = ResponseStatus.COMPLETED;
        submissionChannel = channel;
        submittedAt = LocalDateTime.now();
    }
}
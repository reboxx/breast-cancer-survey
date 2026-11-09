package kw.gov.moh.breastcancer.entity;


import jakarta.persistence.*;
import kw.gov.moh.breastcancer.entity.enums.CampaignStatus;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "breast_cancer_campaign")
@Getter @Setter @NoArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    private Long id;

    @Column(name = "code", nullable = false, length = 50, unique = true)
    private String code;

    @Column(name = "name_ar", nullable = false, length = 200)
    private String nameAr;

    @Column(name = "name_en", nullable = false, length = 200)
    private String nameEn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "version_id", nullable = false)
    private QuestionnaireVersion version;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CampaignStatus status = CampaignStatus.DRAFT;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }

    /** Open, started, and not past end/closure (FR-020/021). */
    public boolean isAvailable(LocalDateTime now) {
        return status == CampaignStatus.OPEN
                && !now.isBefore(startDate)
                && (endDate == null || now.isBefore(endDate))
                && (closedAt == null || now.isBefore(closedAt));
    }
}
package kw.gov.moh.breastcancer.entity;

import jakarta.persistence.*;
import kw.gov.moh.breastcancer.entity.enums.Channel;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "breast_cancer_audit_log")
@Getter @Setter @NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long id;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "participant_ref", columnDefinition = "char(64)")
    private String participantRef;

    /** Plain ID (no relation needed for audit writes). */
    @Column(name = "campaign_id")
    private Long campaignId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", length = 10)
    private Channel channel;

    @Column(name = "actor", nullable = false, length = 100)
    private String actor;

    /** Never store answer content here (NFR-04). */
    @Column(name = "details", length = 1000)
    private String details;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }
}
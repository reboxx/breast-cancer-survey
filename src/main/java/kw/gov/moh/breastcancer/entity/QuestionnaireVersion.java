package kw.gov.moh.breastcancer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "breast_cancer_questionnaire_version")
@Getter @Setter @NoArgsConstructor
public class QuestionnaireVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version_id")
    private Long id;

    @Column(name = "version_no", nullable = false, length = 20, unique = true)
    private String versionNo;

    @Column(name = "intro_text_ar", columnDefinition = "nvarchar(max)")
    private String introTextAr;

    @Column(name = "intro_text_en", columnDefinition = "nvarchar(max)")
    private String introTextEn;

    @Column(name = "thank_you_ar", columnDefinition = "nvarchar(max)")
    private String thankYouAr;

    @Column(name = "thank_you_en", columnDefinition = "nvarchar(max)")
    private String thankYouEn;

    @Column(name = "info_link_url", length = 500)
    private String infoLinkUrl;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "version", fetch = FetchType.LAZY)
    @OrderBy("stepNo ASC")
    private List<SurveyStep> steps = new ArrayList<>();
}
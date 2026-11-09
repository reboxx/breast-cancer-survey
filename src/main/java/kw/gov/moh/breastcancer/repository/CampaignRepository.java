package kw.gov.moh.breastcancer.repository;

import kw.gov.moh.breastcancer.entity.Campaign;
import kw.gov.moh.breastcancer.entity.enums.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Optional<Campaign> findByCode(String code);

    /** The campaign currently open for participation (FR-019/020). */
    @Query("""
           select c from Campaign c
           join fetch c.version
           where c.status = kw.gov.moh.breastcancer.entity.enums.CampaignStatus.OPEN
             and c.startDate <= :now
             and (c.endDate  is null or c.endDate  > :now)
             and (c.closedAt is null or c.closedAt > :now)
           order by c.startDate desc
           limit 1
           """)
    Optional<Campaign> findCurrentAvailable(@Param("now") LocalDateTime now);

    boolean existsByStatus(CampaignStatus status);
}
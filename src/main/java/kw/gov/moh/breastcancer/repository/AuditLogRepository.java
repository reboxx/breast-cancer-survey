package kw.gov.moh.breastcancer.repository;

import kw.gov.moh.breastcancer.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
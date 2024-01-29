package com.Library.Library.Repository;

import com.Library.Library.Entities.AuditDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditDTO,Long> {
}

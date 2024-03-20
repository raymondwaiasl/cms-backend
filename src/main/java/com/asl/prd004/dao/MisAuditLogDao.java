package com.asl.prd004.dao;

import com.asl.prd004.entity.MisAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MisAuditLogDao extends JpaRepository<MisAuditLog, String> {
    @Query(value ="select * from mis_audit_log where if(?1!='',mis_audit_user=?1,1=1) and if(?2!='',mis_audit_operation=?2,1=1) and if(?3!='',create_time=?3,1=1)",nativeQuery = true)
    Page<MisAuditLog> getAllAudit(String auditUser, String auditOperation, String auditCreateTime, Pageable pageable);
}

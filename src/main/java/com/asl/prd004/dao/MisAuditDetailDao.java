package com.asl.prd004.dao;

import com.asl.prd004.dto.RecordAuditDetailDto;
import com.asl.prd004.dto.SubscriptionMsgListDto;
import com.asl.prd004.entity.MisAuditDetail;
import com.asl.prd004.entity.MisAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MisAuditDetailDao extends JpaRepository<MisAuditDetail, String> {

    @Query(value = "select new com.asl.prd004.dto.RecordAuditDetailDto( mad.misAuditDtlId, mad.misAuditDtlAction, mad.misAuditRecId, mad.misAuditRechistBfid, mad.misAuditRechistAfid, mad.misOperator, mad.misOperationTime ) " +
            " from MisAuditDetail mad " +
            " where mad.misAuditRecId = :recordId")
    List<RecordAuditDetailDto> getMisAuditDetailByRecordId(String recordId);
}

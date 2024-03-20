package com.asl.prd004.dao;

import com.asl.prd004.entity.MisSequence;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 86181
 */
public interface MisSequenceDao  extends JpaRepository<MisSequence, String> {
    void deleteBySeqName(String seqName);
}

package com.asl.prd004.dao;

import com.asl.prd004.entity.MisContextDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContextDetailDao extends JpaRepository<MisContextDetail, String> {


    @Modifying
    @Query("delete from MisContextDetail where misContextId =?1")
    void deleteByContextId(String contextId);


    @Query("from MisContextDetail where misContextId =?1")
    List<MisContextDetail> findDetailByContextId(String contextId);

    @Query(value="select max(mc.misContextDetailId) from MisContextDetail mc")
    String getMaxId();

    @Query(value="select max(mc.misContextDetailId) from MisContextDetail mc where mc.misContextId <>?1")
    String getMaxIdNotEqualMiscontextId(String contextId);

    List<MisContextDetail> getMisContextDetailsByMisContextWsId(String id);

    @Query("from MisContextDetail where misContextWsId =?1")
    List<MisContextDetail> findDetailByWsId(String misContextWsId);
}

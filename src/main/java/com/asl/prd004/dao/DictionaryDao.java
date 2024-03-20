package com.asl.prd004.dao;
import com.asl.prd004.dto.DicDto;
import com.asl.prd004.dto.WorkspaceListPageDto;
import com.asl.prd004.entity.MisDataDictionary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DictionaryDao extends JpaRepository<MisDataDictionary, String> {
    @Query(nativeQuery = true,value="select mis_dd_id as `key` ,mis_dd_name as value from mis_data_dictionary mdd  where if(?1!='',mis_dd_name=?1,1=1)")
    Page<Object[]> getAllDictName(String ddName,Pageable pageable);

    @Query( value = "select new com.asl.prd004.dto.DicDto(d.misDdId,d.misDdName) " +
            "from  MisDataDictionary d ")
    Page<DicDto> findAllDictName(Pageable pageable);

    @Query(nativeQuery = true,value="select dicName,keyId,`key`,value from (\n" +
            "select mdd.mis_dd_name as dicName ,mddv.mis_ddv_id as keyId ,mddv.mis_ddv_key as `key`,mddv.mis_ddv_value as value from mis_data_dictionary mdd,mis_data_dictionary_value mddv where mdd.mis_dd_id=?1 and  mdd.mis_dd_id =mddv.mis_dd_id)a ")
    Page<Object[]> queryDictDetails(String id,Pageable pageable);
    @Query(value = "select new com.asl.prd004.dto.DicDto(m.misDdvKey,m.misDdvValue) " +
            "from MisDataDictionaryValue m ,MisDataDictionary d where d.misDdId = m.misDdId and d.misDdName = ?1")
    List<DicDto> getDicByDicName(String dicName);

    @Query(value = "select new com.asl.prd004.dto.DicDto(d.misDdId,d.misDdName) " +
            "from  MisDataDictionary d ")
    List<DicDto> getDicList();
    @Query(value = "select new com.asl.prd004.dto.DicDto(d.misDdvKey,d.misDdvValue) " +
            "from  MisDataDictionaryValue d where misDdId=?1")
    List<DicDto> getDicListById(String id);

    @Transactional
    @Modifying
    @Query(value =
            "UPDATE mis_data_dictionary d " +
               "SET d.mis_dd_name=CASE WHEN ?2 IS NULL THEN d.mis_dd_name ELSE ?2 END," +
                   "d.mis_dd_prop_sql=CASE WHEN ?3 IS NULL THEN d.mis_dd_prop_sql ELSE ?3 END " +
             "WHERE d.mis_dd_id=?1 ",nativeQuery = true)
    Integer updateProp(String id,String dicName,String propSql);
}

package com.asl.prd004.dao;
import com.asl.prd004.entity.MisDataDictionaryValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface DictionaryValueDao extends JpaRepository<MisDataDictionaryValue, String> {

    @Query(nativeQuery = true,value="delete from mis_data_dictionary_value where mis_dd_id = ?1")
    @Modifying
    void delDicDetailsById(String id);

    List<MisDataDictionaryValue> findByMisDdIdAndMisDdvKey(String misDdId,String misDdvKey);
}

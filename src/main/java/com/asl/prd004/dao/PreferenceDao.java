package com.asl.prd004.dao;

import com.asl.prd004.entity.MisPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PreferenceDao extends JpaRepository<MisPreference, String> {
    @Query(nativeQuery = true,value="select mp.mis_preference_id ,mp.mis_preference_name ,mp.mis_preference_value ,mp.mis_preference_visible ,mp.create_time,mp.mis_user_id from mis_preference mp where mp.mis_preference_visible ='0'")
    List<MisPreference> getPreferenceInfo();
}

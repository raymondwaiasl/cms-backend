package com.asl.prd004.dao;

import com.asl.prd004.entity.FormInputRequestindicatorS;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormInputRequestIncidatorDao extends JpaRepository<FormInputRequestindicatorS, String> {

    List<FormInputRequestindicatorS> findFormInputRequestIncidatorByIndCode(String indCode);
}
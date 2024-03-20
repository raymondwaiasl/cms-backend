package com.asl.prd004.utils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;

import java.io.Serializable;

public class SeqGenerator extends UUIDGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws MappingException {
//        System.out.println("asdfasf====="+object.getClass().getName());
        String className = StringUtils.substringAfter(object.getClass().getName(),"com.asl.prd004.entity.");
        String tableName = covertToTableName(className);
        String id = SerialNumberUtils.getTableSequence( tableName );
//        String id = SerialNumberUtils.getTableSequence(   "mis_type_seq");
        if(null != id){
            return  id;
        }
        return super.generate(session,object);
    }

    public static String covertToTableName(String entityName) {
        StringBuilder sb = new StringBuilder();
        char[] charArray = entityName.toCharArray();
        for (char aCharArray : charArray) {
            if (aCharArray >= 'A' && aCharArray <= 'Z') {
                sb.append("_").append(aCharArray);
            } else {
                sb.append(aCharArray);
            }
        }
        String substring = sb.substring(0, 1);
        if (substring.equals("_")) {
            return sb.substring(1).toLowerCase();
        } else {
            return sb.toString().toLowerCase();
        }
    }
}

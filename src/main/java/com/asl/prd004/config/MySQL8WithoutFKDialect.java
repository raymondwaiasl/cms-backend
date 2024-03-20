package com.asl.prd004.config;

import org.hibernate.dialect.InnoDBStorageEngine;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.MySQLStorageEngine;

public class MySQL8WithoutFKDialect extends MySQL5Dialect {

        @Override
        protected MySQLStorageEngine getDefaultMySQLStorageEngine() {
            return InnoDBStorageEngine.INSTANCE;
        }
        /**
         * 生成外键时执行代码
         */
        @Override
        public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {
            return "";
//        return " drop foreign key " + constraintName;
        }



}

package com.asl.prd004.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SQLExecutor {

    public static SQLExecutor sqlExecutor;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        sqlExecutor = this;
    }

    public static List<String> getColumns(String sql) {
        List<String> result = new ArrayList<>();
        SqlRowSet resultSet = sqlExecutor.jdbcTemplate.queryForRowSet(sql);
        SqlRowSetMetaData metaData = resultSet.getMetaData();

        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            System.out.println(metaData.getTableName(i));
            String columnName = metaData.getColumnLabel(i);
            result.add(columnName);
        }
        return result;
    }

    public static String getTableName(String sql) {
        String result = "";
        SqlRowSet resultSet = sqlExecutor.jdbcTemplate.queryForRowSet(sql);
        SqlRowSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            System.out.println("schemaName==="+metaData.getSchemaName(1));
            System.out.println("getTableName==="+metaData.getTableName(i));
            System.out.println("getCatalogName==="+metaData.getCatalogName(i));
            if(metaData.getColumnLabel(i).equals("id")){
                return metaData.getTableName(i);
            }
        }
        return result;
    }

    public static long getTotal(String sql) {
        SqlRowSet resultSet = sqlExecutor.jdbcTemplate.queryForRowSet(sql);
        long i = 0;
        while (resultSet.next()) {
            i++;
        }
        return i;
    }

    private static final String JDBC_URL = "jdbc:mysql://192.168.50.155:3306/MIS?useOldAliasMetadataBehavior=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    public static String getTableAlias(String sql) {
        String tableAlias = "";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            tableAlias = metaData.getTableName(1);
            System.out.println("SQL executed successfully.");
        } catch (SQLException e) {
            System.out.println("SQL execution failed. Error: " + e.getMessage());
        }
        return tableAlias;
    }




    public static void main(String[] args) {
//        String sql = "SELECT * FROM mis_user";
        String sql = "select u.mis_user_name as name1, g.mis_group_id ,u.mis_user_id  from mis_user as u " +
                "left join (select * from mis_group) g on g.mis_group_id = u.current_group" +
                " where 1 = 2";
//        List<String> queryResult = getColumns(sql);
        String tableAlias = getTableAlias(sql);
        System.out.println(tableAlias + ".id");
        Pattern pattern = Pattern.compile(tableAlias + ".id");
        Matcher matcher = pattern.matcher(sql);
        StringBuilder stringBuilder = new StringBuilder(sql);
        stringBuilder.insert(sql.toLowerCase().indexOf("select ") + 7, tableAlias + ".id, ");
        System.out.println(sql.toLowerCase().indexOf("select ") + 7);
        System.out.println(stringBuilder);
        if(matcher.find()){
            System.out.println("id已存在");
        }


//        for (String row : queryResult) {
//            System.out.println(row);
//        }
    }
}
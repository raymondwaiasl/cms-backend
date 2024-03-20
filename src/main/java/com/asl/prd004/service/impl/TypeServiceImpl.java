package com.asl.prd004.service.impl;

import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.constant.Constants;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.asl.prd004.dao.*;
import com.asl.prd004.entity.MisGpTypePermission;
import com.asl.prd004.enums.TableMgmtColumnInputType;
import com.asl.prd004.utils.ConstUtils;
import com.asl.prd004.utils.DbSeqUtil;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisColumn;
import com.asl.prd004.entity.MisType;
import com.asl.prd004.service.ITypeService;
import com.asl.prd004.utils.SerialNumberUtils;
import com.asl.prd004.utils.StrUtil;
import com.asl.prd004.vo.CalcColumnQueryResultVO;
import com.asl.prd004.vo.MisColumnComputeFormulaVO;
import com.asl.prd004.vo.MisColumnVO;
import com.asl.prd004.vo.TypeVO;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class TypeServiceImpl implements ITypeService {

    @Autowired
    MisTypeDao misTypeDao;
    @Autowired
    MisColumnDao misColumnDao;
    @Autowired
    DictionaryDao dictionaryDao;

    @Autowired
    BiToolDao biToolDao;

    @Autowired
    MisSequenceDao sequenceDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    MisGpPermissionDao misGpPermissionDao;

    @Override
    public List<DicDto> getTableNames() {
        String sql = "select  t.TABLE_Name  FROM information_schema.TABLES t WHERE t.TABLE_SCHEMA = 'MIS'";
        List<String> res = entityManager.createNativeQuery(sql).getResultList();
        List<DicDto> dicList = new ArrayList<>();
        for (String s : res) {
            DicDto d = new DicDto();
            d.setKey(s);
            d.setValue(s);
            dicList.add(d);
        }
        return dicList;
    }


    @Override
    public TypeVO selectTypeById(String id) {
//        TypeDto typeDto = new TypeDto();
//        Optional<TypeListDto> typeListDtoOptional = Optional.ofNullable(misTypeDao.findTypeByMisTypeId(id));
//        if (typeListDtoOptional.isPresent()) {
//            TypeListDto typeListDto = typeListDtoOptional.get();
//            if (typeListDto != null) {
//                typeDto.setMisTypeId(typeListDto.getMisTypeId());
//                typeDto.setMisTypeName(typeListDto.getMisTypeName());
//                typeDto.setMisTypeLabel(typeListDto.getMisTypeLabel());
//                List<MisColumn> misColumnList = misColumnDao.findByMisTypeId(typeListDto.getMisTypeId());
//                typeDto.setMisColumnList(misColumnList);
//            }
//        }
//        return typeDto;

        TypeVO typeVO = new TypeVO();
        Optional<TypeListDto> typeListDtoOptional = Optional.ofNullable(misTypeDao.findTypeByMisTypeId(id));
        if (typeListDtoOptional.isPresent()) {
            TypeListDto typeListDto = typeListDtoOptional.get();
            if (typeListDto != null) {
                typeVO.setMisTypeId(typeListDto.getMisTypeId());
                typeVO.setMisTypeName(typeListDto.getMisTypeName());
                typeVO.setMisTypeLabel(typeListDto.getMisTypeLabel());
                List<MisColumn> misColumnList = misColumnDao.findByMisTypeId(typeListDto.getMisTypeId());
                List<MisColumnVO> misColumnVoList = misColumnList.stream().map(entity -> {
                    MisColumnVO misColumnVO = BeanUtil.copyProperties(entity, MisColumnVO.class,"misColumnComputeFormula");
                    if (StrUtil.isNotBlank(entity.getMisColumnComputeFormula())) {
                        misColumnVO.setMisColumnComputeFormula(
                            JSON.parseObject(entity.getMisColumnComputeFormula(), MisColumnComputeFormulaVO.class));
                    }
                    return misColumnVO;
                }).collect(Collectors.toList());
                typeVO.setMisColumnList(misColumnVoList);
            }
        }

        return typeVO;
    }

    @Override
    public TypeIdDto addNewType(MisType misType) {
        MisType type;
        MisType t = new MisType();

        if(validateDuplicateTableName(misType.getMisTypeName())>0){
            throw new RuntimeException("The table name has existed!");
        }

        t.setMisTypeLabel(misType.getMisTypeLabel());
        t.setMisTypeName(misType.getMisTypeName());
        type = misTypeDao.saveAndFlush(t);
        TypeIdDto typeIdDto = new TypeIdDto();
        typeIdDto.setId(type.getMisTypeId());

        DbSeqUtil.initSeq(type.getMisTypeName() + "_s");
        addTableTypeToDb(misType);
        createTrigger(misType.getMisTypeName() + "_s");

        return typeIdDto;
    }

    @Override
    public boolean updateType(MisType misType) {
        MisType t = new MisType();
        t.setMisTypeId(misType.getMisTypeId());
        t.setMisTypeLabel(misType.getMisTypeLabel());
        misTypeDao.saveAndFlush(t);
        return true;
    }

    @Override
    public boolean deleteType(TypeIdDto dto) {
        Optional<MisType> misTypeOptional = misTypeDao.findById(dto.getId());
        if (misTypeOptional.isPresent()) {
            MisType misType = misTypeOptional.get();
            if (misType != null) {
                dropTableToDb(misType);
                dropTrigger(misType.getMisTypeName() + "_s");
                sequenceDao.deleteBySeqName(misType.getMisTypeName() + "_s");
                misTypeDao.deleteById(dto.getId());
                misColumnDao.deleteByMisTypeId(dto.getId());
                biToolDao.deleteByMisBiConfigTypeId(dto.getId());
                jdbcTemplate.execute(dropRepeatingFieldTableSql(misType.getMisTypeName()));
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    @Override
    public boolean addNewColumn(MisColumn column) {
        List<MisColumn> columns = misColumnDao.findByMisTypeIdAndMisColumnName(column.getMisTypeId(), column.getMisColumnName());
        if(columns.size() == 0){
            MisColumn misColumn = misColumnDao.saveAndFlush(column);
            addTypeColToDb(column);
//            List<List<String>> res = entityManager.createNativeQuery("SHOW COLUMNS FROM testlog_s").getResultList();
//            System.out.println(res);
            return true;
        }else {
            throw new RuntimeException("The column name has existed!");
        }

    }

    @Override
    public boolean updateColumn(MisColumn column) {
        List<MisColumn> columns = misColumnDao.findByMisTypeIdAndMisColumnName(column.getMisTypeId(), column.getMisColumnName());
        if(columns.size() == 1){
            misColumnDao.saveAndFlush(column);
            modifyColToDb(column);
            return true;
        }else {
            throw new RuntimeException("The column name has existed!");
        }

    }

    @Override
    public boolean deleteColumn(TypeIdDto dto) {
        Optional<MisColumn> misColumnOptional =  misColumnDao.findById(dto.getId());
        if (misColumnOptional.isPresent()) {
            MisColumn misColumn = misColumnOptional.get();
            if (misColumn != null) {
                dropColToDb(misColumn);
                misColumnDao.deleteById(dto.getId());

                if(TableMgmtColumnInputType.REPEATING_FIELD.getCode().equals(misColumn)){
                    //当表的最后一个REPEATING_FIELD字段删除时,删除掉记录字段的_r表
                    List<MisColumn> columns = misColumnDao.findByMisTypeId(misColumn.getMisTypeId());
                    if(columns.stream().noneMatch(c -> TableMgmtColumnInputType.REPEATING_FIELD.getCode().equals(c.getMisColumnInputType()))){
                        jdbcTemplate.execute(dropRepeatingFieldTableSql(misColumn));
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    @Override
    public PageDataDto getAllTypes(PageableDto pageable) {
        Pageable page;
        if (StringUtils.isNotEmpty(pageable.getSortModel().getField())) {
            String sortField = pageable.getSortModel().getField();
            switch (sortField) {
                case "misTypeId":
                    sortField = "misTypeId";
                    break;
                case "misTypeLabel":
                    sortField = "misTypeLabel";
                    break;
                case "misTypeName":
                    sortField = "misTypeName";
                    break;
            }
            if (pageable.getSortModel().getSort().equalsIgnoreCase("asc")) {
                page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize(), Sort.by(sortField).ascending());
            } else {
                page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize(), Sort.by(sortField).descending());
            }
        } else {
            page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize());
        }
        Page<TypeListDto> typePage = misTypeDao.findAllTypePageable(page);
        List<TypeListDto> types = typePage.getContent();
        PageDataDto pageData = new PageDataDto();
        pageData.setData(types);
        pageData.setTotal(typePage.getTotalElements());
        return pageData;
    }

    @Override
    public List<DicDto> getDicList(){
        return dictionaryDao.getDicList();
    }

    private int validateDuplicateTableName(String name) {
        List<MisType> types = misTypeDao.getMisTypesByMisTypeName(name);
        return types.size();
    }


    private void addTableTypeToDb(MisType misType) {
        String sql = createTableSql(misType);
        for(int i=1;i<=2;i++) {//添加了draft表,所以多创建了draft逻辑
            String sql1 = createTableForLogSql(misType,i);
            jdbcTemplate.execute(sql1);
        }
        jdbcTemplate.execute(sql);
    }

    private void addTypeColToDb(MisColumn misColumn) {
        for(int i=1;i<=4;i++) {
            String sql = addColSql(misColumn,i);
            //String sql1 = alterColSql(Constants.ADD_COL,getTableName(misColumn,i), misColumn);
            jdbcTemplate.execute(sql);
            //jdbcTemplate.execute(sql1);
            if(i==1 || i==2) {
                alterTrigger(getTableName(misColumn, i));
            }
            if(TableMgmtColumnInputType.REPEATING_FIELD.getCode().equals(misColumn.getMisColumnInputType())){
                sql = createRepeatingFieldTableSql(misColumn,i);
                jdbcTemplate.execute(sql);
            }
        }
        /*String sql = addColSql(misColumn);
        String sql1 = alterColSql(Constants.ADD_COL,getTableName(misColumn) + "_log", misColumn);
        jdbcTemplate.execute(sql);
        jdbcTemplate.execute(sql1);
        alterTrigger(getTableName(misColumn));*/

        //If there is a repeating field creates, a _r table will be created.
       /* if(TableMgmtColumnInputType.REPEATING_FIELD.getCode().equals(misColumn.getMisColumnInputType())){
            sql = createRepeatingFieldTableSql(misColumn);
            jdbcTemplate.execute(sql);
        }*/
    }

    private String createRepeatingFieldTableSql(MisColumn misColumn,int i){
        return
            String.format(
                "create table if not exists %s(" +
                    "`id` varchar(16) NOT NULL," +
                    "`mis_column_id` varchar(16) NOT NULL," +
                    "`value` varchar(%s) DEFAULT NULL" +
                ")", getTableName(misColumn,"_r",i), misColumn.getMisColumnLength())
            ;
    }

    private String dropRepeatingFieldTableSql(MisColumn misColumn){
        return String.format("drop table if exists %s",getTableName(misColumn,"_r",1));
    }

    private String dropRepeatingFieldTableSql(String misTypeName){
        return String.format("drop table if exists %s",(misTypeName + "_r"));
    }

    private void modifyColToDb(MisColumn misColumn) {
        for(int i=1;i<=4;i++) {
            String sql = modifyColSql(misColumn,i);
            //String sql1 = alterColSql(Constants.MODIFY_COL, getTableName(misColumn) + "_log", misColumn);
            jdbcTemplate.execute(sql);
            //jdbcTemplate.execute(sql1);
            if(i==1 || i==2) {
                alterTrigger(getTableName(misColumn, i));
            }
        }
        /*String sql = modifyColSql(misColumn);
        String sql1 = alterColSql(Constants.MODIFY_COL,getTableName(misColumn) + "_log", misColumn);
        jdbcTemplate.execute(sql);
        jdbcTemplate.execute(sql1);
        alterTrigger(getTableName(misColumn));*/
    }

    private void dropColToDb(MisColumn misColumn) {
       /* String sql = dropColSql(misColumn);
        String sql1 = alterColSql(Constants.DELETE_COL,getTableName(misColumn) + "_log", misColumn);
        jdbcTemplate.execute(sql);
        jdbcTemplate.execute(sql1);
        alterTrigger(getTableName(misColumn));*/
        for(int i=1;i<=4;i++) {
            String sql = dropColSql(misColumn,i);
            //String sql1 = alterColSql(Constants.DELETE_COL, getTableName(misColumn) + "_log", misColumn);
            jdbcTemplate.execute(sql);
            //jdbcTemplate.execute(sql1);
            if(i==1 || i==2) {
                alterTrigger(getTableName(misColumn, i));
            }
        }
    }

    private void dropTableToDb(MisType misType) {
        String sql1 = dropTableSql(misType, "s");
        String sql2 = dropTableSql(misType, "r");
        String sql3 = dropTableSql(misType, "s_log");
        String sql4 = dropTableSql(misType, ConstUtils.TYPEDRRAFTLOG);
        jdbcTemplate.execute(sql1);
        jdbcTemplate.execute(sql2);
        jdbcTemplate.execute(sql3);
        jdbcTemplate.execute(sql4);
    }

    private String createTableSql(MisType misType) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE `");
        builder.append(misType.getMisTypeName());
        builder.append("_s` (\n" +
                "`id` varchar(16) not null,\n" +
                "`creation_date` datetime  not null,\n" +
                "`creator_user_id` varchar(16) not null,\n" +
                "`updated_date` datetime not null,\n" +
                "`updated_user_id` varchar(16) not null," +
                "`folder_id` varchar(16) not null," +
                "`mis_permission_id` varchar(16) not null," +
                "PRIMARY KEY (`id`)" +
                ")");
        return builder.toString();

    }

    private String createTableForLogSql(MisType misType,int i) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE `");
        builder.append(misType.getMisTypeName());
        if(i==1) {
            builder.append("_s_log` (\n" +
                    "`uid` INT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "`action` varchar(16)  not null,\n" +
                    "`transaction_date` datetime  not null,\n" +
                    "`id` varchar(16) not null,\n" +
                    "`creation_date` datetime  not null,\n" +
                    "`creator_user_id` varchar(16) not null,\n" +
                    "`updated_date` datetime not null,\n" +
                    "`updated_user_id` varchar(16) not null," +
                    "`folder_id` varchar(16) not null," +
                    "`mis_permission_id` varchar(16) not null" +
                    ")");
        }else{
            builder.append("_draft_log` (\n" +
                    "`uid` INT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "`action` varchar(16)  not null,\n" +
                    "`transaction_date` datetime  not null,\n" +
                    "`id` varchar(16) not null,\n" +
                    "`creation_date` datetime  not null,\n" +
                    "`creator_user_id` varchar(16) not null,\n" +
                    "`updated_date` datetime not null,\n" +
                    "`updated_user_id` varchar(16) not null," +
                    "`folder_id` varchar(16) not null," +
                    "`mis_permission_id` varchar(16) not null" +
                    ")");
        }
        return builder.toString();
    }

    //ALTER TABLE `asl-react`.dm_type_r ADD Column1 varchar(100) NULL;
    private String addColSql(MisColumn misColumn,int i) {
        return alterColSql(Constants.ADD_COL,getTableName(misColumn,i),misColumn);
    }

    //ALTER TABLE `asl-react`.dm_type_r MODIFY COLUMN attr_name varchar(41) NULL;
    private String modifyColSql(MisColumn misColumn,int i) {
        return alterColSql(Constants.MODIFY_COL,getTableName(misColumn,i),misColumn);
    }

    //ALTER TABLE MIS.asdas1_s DROP COLUMN updated_user_id;
    private String dropColSql(MisColumn misColumn,int i) {
        return alterColSql(Constants.DELETE_COL,getTableName(misColumn,i),misColumn);
    }

    //drop table if exists sys_post;
    private String dropTableSql(MisType misType, String suffix) {
        StringBuilder builder = new StringBuilder();
        builder.append("drop table if exists ");
        builder.append(misType.getMisTypeName());
        builder.append("_");
        builder.append(suffix);
        return builder.toString();
    }

    private String getTableName(MisColumn misColumn,int i) {
        return getTableName(misColumn,"_s",i);
    }

    private String getTableName(MisColumn misColumn,String suffix,int i) {
        String tableName = "";
        Optional<MisType> s = misTypeDao.findById(misColumn.getMisTypeId());
        if (s.isPresent()) {
            MisType misType = s.get();
            if (misType != null&&i==1) {
                tableName = misType.getMisTypeName() + ConstUtils.TYPEMAIN;
            }else if(misType != null&&i==2){
                tableName = misType.getMisTypeName() + ConstUtils.TYPEDRAFT;
            }else if(misType != null&&i==3){
                tableName = misType.getMisTypeName() + ConstUtils.TYPELOG;
            }else{
                tableName = misType.getMisTypeName() + ConstUtils.TYPEDRRAFTLOG;
            }
        }
        return tableName;
    }

    private String getColName(MisColumn misColumn) {
        return misColumn.getMisColumnName();
    }

    private String getColType(MisColumn misColumn) {
        if(TableMgmtColumnInputType.HYPERLINK.getCode().equals(misColumn.getMisColumnInputType())){
            return " text ";
        }

        String colType = "";
        switch (misColumn.getMisColumnType()) {
            //Boolean
            case "0":
                colType = " tinyint(1) ";
                break;
                //String
            case "1":
                colType = " varchar(" + misColumn.getMisColumnLength() + ") ";
                break;
            //Integer
            case "2":
                colType = " int ";
                break;
            //ID
            case "3":
                colType = " varchar(16) ";
                break;
            //Time
            case "4":
                colType = " datetime ";
                break;
            //Time
            case "5":
                colType = " double ";
                break;
            case "6":
                colType = " text ";
                break;
        }
        return colType;
    }

    private String alterColSql(String type, String tableName, MisColumn misColumn) {
        StringBuilder builder = new StringBuilder();
        builder.append("ALTER TABLE `");
        builder.append(tableName);
        builder.append("` " +
                type +
                "  `");
        if(Constants.DELETE_COL.equals(type)){
            builder.append(getColName(misColumn) + "`;");
        }else {
            builder.append(getColName(misColumn));
            builder.append("` ");
            builder.append(getColType(misColumn));
            if ("Y".equals(misColumn.getMisColumnWidth())) {
                builder.append(" NULL ");
            } else if ("N".equals(misColumn.getMisColumnWidth())) {
                builder.append(" NOT NULL ");
            }
            if (misColumn.getMisColumnType().equals("4")) {
                builder.append("  DEFAULT CURRENT_TIMESTAMP() ");
            }
        }
        return builder.toString();
    }

    private void createTrigger(String tableName){
        jdbcTemplate.execute(triggerSql(tableName, Constants.CREATE_OPERATION, Constants.INSERT_OPERATION));
        jdbcTemplate.execute(triggerSql(tableName, Constants.CREATE_OPERATION, Constants.UPDATE_OPERATION));
        jdbcTemplate.execute(triggerSql(tableName, Constants.CREATE_OPERATION, Constants.DELETE_OPERATION));
    }

    private void alterTrigger(String tableName){
        jdbcTemplate.execute(triggerSql(tableName, Constants.DROP_OPERATION, Constants.INSERT_OPERATION));
        jdbcTemplate.execute(triggerSql(tableName, Constants.CREATE_OPERATION, Constants.INSERT_OPERATION));
        jdbcTemplate.execute(triggerSql(tableName, Constants.DROP_OPERATION, Constants.UPDATE_OPERATION));
        jdbcTemplate.execute(triggerSql(tableName, Constants.CREATE_OPERATION, Constants.UPDATE_OPERATION));
        jdbcTemplate.execute(triggerSql(tableName, Constants.DROP_OPERATION, Constants.DELETE_OPERATION));
        jdbcTemplate.execute(triggerSql(tableName, Constants.CREATE_OPERATION, Constants.DELETE_OPERATION));
    }

    private void dropTrigger(String tableName){
        jdbcTemplate.execute(triggerSql(tableName, Constants.DROP_OPERATION, Constants.INSERT_OPERATION));
        jdbcTemplate.execute(triggerSql(tableName, Constants.DROP_OPERATION, Constants.UPDATE_OPERATION));
        jdbcTemplate.execute(triggerSql(tableName, Constants.DROP_OPERATION, Constants.DELETE_OPERATION));
    }

    private String triggerSql(String tableName,String triggerOperation,String tableOperation){
        List<Object[]> fields = entityManager.createNativeQuery("SHOW COLUMNS FROM " + tableName).getResultList();
        StringBuilder fieldBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        for(int i = 0; i < fields.size(); i ++){
            fieldBuilder.append(", " + fields.get(i)[0]);
            valueBuilder.append(", NEW." +fields.get(i)[0]);
        }
        StringBuilder triggerSql = new StringBuilder();
        if(Constants.DROP_OPERATION.equals(triggerOperation)){
            triggerSql.append("DROP TRIGGER IF EXISTS " + tableName + "_" + tableOperation.toLowerCase() + "_trigger;");
        }else{
            triggerSql.append(triggerOperation + " TRIGGER " + tableName + "_" + tableOperation.toLowerCase() + "_trigger AFTER " + tableOperation + " ON " + tableName + " FOR EACH ROW BEGIN " +
                    "INSERT INTO " + tableName + "_log (action, transaction_date");
            if(Constants.DELETE_OPERATION.equals(tableOperation)){
                triggerSql.append(", id,creation_date, creator_user_id, updated_date, updated_user_id, folder_id, mis_permission_id) VALUES ('DELETE', CURRENT_TIMESTAMP, OLD.id,OLD.creation_date, OLD.creator_user_id, OLD.updated_date, OLD.updated_user_id, OLD.folder_id, OLD.mis_permission_id");
            }else{
                //add fields
                triggerSql.append(fieldBuilder);
                triggerSql.append(") VALUES ('" + tableOperation + "', CURRENT_TIMESTAMP ");
                //add values
                triggerSql.append(valueBuilder);
            }
            triggerSql.append("); END;");
        }
        return triggerSql.toString();
    }

    private String getFieldStr(String tableName){

        return "";
    }

    @Override
    public List<TypeListDto> getAllTypes(){
        return  misTypeDao.getTypeAll();
    }
    
    @Override
    public List<MisColumn> queryColumnByTypeId(String id) {
        return  misColumnDao.findByMisTypeId(id);
    }

    @Transactional
    @Override
    public TypeIdDto addNewTypeAndDraf(TypeAndDrafDto dto) {
        MisType type;
        MisType t = new MisType();

        TypeIdDto typeIdDto = null;
        try {
            if(validateDuplicateTableName(dto.getMisTypeName())>0){
                throw new RuntimeException("The table name has existed!");
            }

            t.setMisTypeLabel(dto.getMisTypeLabel());
            t.setMisTypeName(dto.getMisTypeName());
            type = misTypeDao.saveAndFlush(t);
            typeIdDto = new TypeIdDto();
            typeIdDto.setId(type.getMisTypeId());
            DbSeqUtil.initSeq(type.getMisTypeName() + ConstUtils.TYPEMAIN);
            addTableTypeToDb(t);
            createTrigger(type.getMisTypeName() + ConstUtils.TYPEMAIN);
            if(dto.getDraftTable()!=null&&"Y".equals(dto.getDraftTable())){//draft为Y,表示要建draft表并保存permission数据
                DbSeqUtil.initSeq(type.getMisTypeName() + ConstUtils.TYPEDRAFT);
                createDraftTableSql(t);
                createTrigger(type.getMisTypeName() + ConstUtils.TYPEDRAFT);
                String groupPermissionData=dto.getGroupPerData();
                if(groupPermissionData!=null&&groupPermissionData!="") {
                    String misGpId="";
                    org.json.JSONObject jsonObject = new JSONObject(groupPermissionData);
                    Iterator<String> keys = jsonObject.keys();

                    List<String> newList = new ArrayList<>();
                    keys.forEachRemaining(newList::add);
                    for(int i=0;i<newList.size();i++) {
                        String keyValue=newList.get(i);
                        MisGpTypePermission misGpTypePermission=new MisGpTypePermission();
                        int j=0;
                        Iterator<String> keysNew = jsonObject.keys();
                        misGpId=keyValue.replace("Access","").replace("Create","").replace("Edit","").replace("Delete","");
                        while (keysNew.hasNext()) {
                            String key = keysNew.next();
                            Boolean value = jsonObject.getBoolean(keyValue);
                            if (key.contains(misGpId) && key.contains("Access")) {
                                misGpTypePermission.setAccess(value == true ? ConstUtils.TYPEPERMISSIONTRUE : ConstUtils.TYPEPERMISSIONFALSE);
                                j += 1;
                            } else if (key.contains(misGpId) && key.contains("Create")) {
                                misGpTypePermission.setDirectCreate(value == true ? ConstUtils.TYPEPERMISSIONTRUE : ConstUtils.TYPEPERMISSIONFALSE);
                                j += 1;
                            }else if (key.contains(misGpId) && key.contains("Edit")) {
                                misGpTypePermission.setDirectCreate(value == true ? ConstUtils.TYPEPERMISSIONTRUE : ConstUtils.TYPEPERMISSIONFALSE);
                                j += 1;
                            }else if (key.contains(misGpId) && key.contains("Delete")) {
                                misGpTypePermission.setDirectDelete(value == true ? ConstUtils.TYPEPERMISSIONTRUE : ConstUtils.TYPEPERMISSIONFALSE);
                                j += 1;
                            }
                        }

                        if(j!=0){
                            misGpTypePermission.setMisTypeId(type.getMisTypeId());
                            misGpTypePermission.setMisGpId(misGpId);
                            misGpTypePermission.setMisGpTypePermissionId(SerialNumberUtils.getTableSequence(type.getMisTypeName() + "_draft"));
                            misGpTypePermission.setCreateBy(ContextHolder.getUserId());
                            misGpTypePermission.setUpdateBy(ContextHolder.getUserId());
                            misGpPermissionDao.saveAndFlush(misGpTypePermission);
                            newList.remove(keyValue);
                            //i=0;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return typeIdDto;

    }
    private void createDraftTableSql(MisType misType) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE `");
        builder.append(misType.getMisTypeName());
        builder.append("_draft` (\n" +
                "`id` varchar(16) not null,\n" +
                "`creation_date` datetime  not null,\n" +
                "`creator_user_id` varchar(16) not null,\n" +
                "`updated_date` datetime not null,\n" +
                "`updated_user_id` varchar(16) not null," +
                "`folder_id` varchar(16) not null," +
                "`mis_permission_id` varchar(16) not null," +
                "PRIMARY KEY (`id`)" +
                ")");
        jdbcTemplate.execute(builder.toString());
    }
}

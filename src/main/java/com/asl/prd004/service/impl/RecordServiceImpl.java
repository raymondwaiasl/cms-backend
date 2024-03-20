package com.asl.prd004.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.asl.prd004.config.DefinitionException;
import com.asl.prd004.controller.RecordMangeController;
import com.asl.prd004.enums.TableMgmtColumnInputType;
import com.asl.prd004.enums.TableMgmtColumnType;
import com.asl.prd004.service.IRecordMangeService;
import com.asl.prd004.utils.ConstUtils;
import com.asl.prd004.utils.StrUtil;
import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.*;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.*;
import com.asl.prd004.enums.ConditionEnum;
import com.asl.prd004.service.IDictionaryService;
import com.asl.prd004.service.IFolderService;
import com.asl.prd004.service.IRecordService;
import com.asl.prd004.vo.CalcColumnQueryResultVO;
import com.asl.prd004.vo.MisColumnComputeFormulaVO;
import com.github.wenhao.jpa.Specifications;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RecordServiceImpl implements IRecordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MisTypeDao typeDao;

    @Autowired
    private MisColumnDao columnDao;

    @Autowired
    private MisImportHistDao misImportHistDao;

    @Autowired
    private FolderDao folderDao;

    @Autowired
    private MisPermissionDetailDao permissionDetailDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private DictionaryDao dictionaryDao;

    @Autowired
    private DictionaryValueDao dictionaryValueDao;

    @Autowired
    private IDictionaryService dictionaryService;

    @Autowired
    private MisCrossRefDao misCrossRefDao;

    @Autowired
    private MisPropertyConfigDetailDao misPropertyConfigDetailDao;

    @Autowired
    private IFolderService folderService;
    @Autowired
    private MisPropertyColumnPermissionDao misPropertyColumnPermissionDao;
    @Autowired
    private MisColumnDao misColumnDao;
    @Autowired
    IRecordMangeService recordMange;

    @Autowired
    private MisAuditDetailDao auditDetailDao;

    @Autowired
    WidgetColumnDao widgetColumnDao;
	
	@Autowired
    private MisGpPermissionDao misGpPermissionDao;

    @Override
    public List<MisColumn> getColumnList(String columns) {
        List<MisColumn> list = new ArrayList<>();
        String column[] = columns.split(",");
        for (int i = 0; i < column.length; i++) {
            Optional<MisColumn> misColumn = columnDao.findById(column[i]);
            if (misColumn.isPresent()) {
                list.add(misColumn.get());
            }
        }
        return list;
    }

    @Override
    public List getRecordList(String typeId, List<MisColumn> columnList, org.json.JSONArray conditionData, String folderId) throws JSONException {
        MisType type = typeDao.findById(typeId).get();
        StringBuilder querySQL = new StringBuilder("SELECT id,");
        StringBuilder columnSQL = new StringBuilder();
        String col = "";
        String dic = "";
        if (columnList.size() > 0) {
            //String column[] = columns.split(",");
            for (int i = 0; i < columnList.size(); i++) {
                MisColumn misColumn = columnList.get(i);
                columnSQL.append(misColumn.getMisColumnName() + ",");
                if (StringUtils.isNotEmpty(misColumn.getMisColumnDictionary())) {
                    dic += i + "&" + misColumn.getMisColumnDictionary() + ",";
                }
            }
            col = columnSQL.toString() + "mis_permission_id";
        } else {
            List<MisColumn> columnList2 = columnDao.findByMisTypeId(typeId);
            for (int i = 0; i < columnList2.size(); i++) {
                col += columnList2.get(i).getMisColumnName() + ",";
                if (StringUtils.isNotEmpty(columnList2.get(i).getMisColumnDictionary())) {
                    dic += i + "&" + columnList2.get(i).getMisColumnDictionary() + ",";
                }
            }
            col = col + "mis_permission_id";
        }
        querySQL.append(col);

        String ids = folderService.getFolderChildList(folderId);
        String[] folderArray = ids.split(",");
        StringBuilder folderStr = new StringBuilder("(");
        for (int i = 1; i < folderArray.length; i++) {
            if (i != folderArray.length - 1) {
                folderStr.append("'").append(folderArray[i]).append("',");
            } else {
                folderStr.append("'").append(folderArray[i]).append("'");
            }
        }
        folderStr.append(")");

        querySQL.append(" FROM " + type.getMisTypeName() + "_s where folder_id in " + folderStr.toString());
        querySQL.append(" and mis_permission_id in (select mis_permission_id from mis_permission_detail mpd where mis_pd_right in('3','5','7')) ");
        StringBuilder whereSQL = new StringBuilder("");


        for (int i = 0; i < conditionData.length(); i++) {
            JSONObject cfc = (JSONObject) conditionData.get(i);
            if (StringUtils.isNotEmpty((String) cfc.get("misQfc2ColumnId"))) {
                Optional<MisColumn> misColumn = columnDao.findById((String) cfc.get("misQfc2ColumnId"));
                if (i == 0) {
                    whereSQL.append(" and ");
                }

                if (!"".equals(misColumn.get().getMisColumnName()) && !"".equals(cfc.getString("misQfc2Condition")) && !"".equals(cfc.getString("misQfc2Value"))) {

                    String misRelation = cfc.getString("misRelation").equals("null") ? "" : cfc.getString("misRelation");
                    if ("0".equals(cfc.getString("misQfc2Condition"))) {
                        whereSQL.append(" " + misRelation + " (" + misColumn.get().getMisColumnName() + " like '%" + cfc.getString("misQfc2Value") + "%')");
                    }
                    if ("1".equals(cfc.getString("misQfc2Condition"))) {
                        whereSQL.append(" " + misRelation + " (" + misColumn.get().getMisColumnName() + " = '" + cfc.getString("misQfc2Value") + "')");
                    }
                    if ("2".equals(cfc.getString("misQfc2Condition"))) {
                        whereSQL.append(" " + misRelation + " (" + misColumn.get().getMisColumnName() + " <= '" + cfc.getString("misQfc2Value") + "')");
                    }
                    if ("3".equals(cfc.getString("misQfc2Condition"))) {
                        whereSQL.append(" " + misRelation + " (" + misColumn.get().getMisColumnName() + " >= '" + cfc.getString("misQfc2Value") + "')");
                    }
                }
            }
        }

        Query query = entityManager.createNativeQuery(querySQL.toString() + whereSQL.toString());
        List list = query.getResultList();
        List result = new ArrayList();
        List res = new ArrayList();
        String userId = ContextHolder.getUserId();
        for (int i = 0; i < list.size(); i++) {
            Boolean isRead = false;
            Object[] object = (Object[]) list.get(i);
            List<MisPermissionDetail> detailList = permissionDetailDao.getListByPermissionId((String) object[object.length - 1]);
            breakRead:
            for (int j = 0; j < detailList.size(); j++) {
                if ("3".equals(detailList.get(j).getMisPdRight()) || "5".equals(detailList.get(j).getMisPdRight()) || "7".equals(detailList.get(j).getMisPdRight())) {
                    if ("4".equals(detailList.get(j).getMisPdType()) && userId.equals(detailList.get(j).getMisPdPerformerId())) {
                        isRead = true;
                        break breakRead;
                    }
                    if ("3".equals(detailList.get(j).getMisPdType())) {
                        String childIds = memberDao.queryChildNodeIds(detailList.get(j).getMisPdPerformerId());
                        if (null != childIds) {
                            if (childIds.contains(userId)) {
                                isRead = true;
                                break breakRead;
                            }
                        }
                    }
                }
            }
            if (isRead) {
                result.add(object);
            }
        }
        if (StringUtils.isNotEmpty(dic)) {
            for (int i = 0; i < result.size(); i++) {
                Object[] object = (Object[]) result.get(i);
                String[] dics = dic.split(",");
                for (int j = 0; j < dics.length; j++) {
                    String[] strs = dics[j].split("&");
                    Integer num = Integer.parseInt(strs[0]);
                    List<DicDto> dicList = dictionaryService.getDicEnumAndSqlListByDicId(strs[1]);

                    for (DicDto dicItem : dicList) {
                        if (dicItem.getKey().equals(String.valueOf(object[num + 1]))) {
                            object[num + 1] = dicItem.getValue();
                            break;
                        }
                    }
                }
                res.add(object);
            }
            return res;
        } else {
            return result;
        }
    }

    private List filterData(List list) {
        List result = new ArrayList();
        String userId = ContextHolder.getUserId();
        for (int i = 0; i < list.size(); i++) {
            Boolean isRead = false;
            Object[] object = (Object[]) list.get(i);
            List<MisPermissionDetail> detailList = permissionDetailDao.getListByPermissionId((String) object[object.length - 1]);
            breakRead:
            for (int j = 0; j < detailList.size(); j++) {
                if ("3".equals(detailList.get(j).getMisPdRight()) || "5".equals(detailList.get(j).getMisPdRight()) || "7".equals(detailList.get(j).getMisPdRight())) {
                    if ("4".equals(detailList.get(j).getMisPdType()) && userId.equals(detailList.get(j).getMisPdPerformerId())) {
                        isRead = true;
                        break breakRead;
                    }
                    if ("3".equals(detailList.get(j).getMisPdType())) {
                        String childIds = memberDao.queryChildNodeIds(detailList.get(j).getMisPdPerformerId());
                        if (null != childIds) {
                            if (childIds.contains(userId)) {
                                isRead = true;
                                break breakRead;
                            }
                        }
                    }
                }
            }
            if (isRead) {
                result.add(object);
            }
        }
        return result;
    }

    @Override
    public SearchRecordVO searchRecord(SearchRecordDTO dto) {
        List<String> tableIds = new ArrayList<>();
        List<MisColumn> columnList = new ArrayList<>();
        Set<String> currentColumnIds = new HashSet<>();//存放已经加入columnList的列id
        Map<String,List<MisQueryFormCondition>> tableId2conditions = new HashMap<>();
        String mainTableId = dto.getTypeId();
        tableIds.add(mainTableId);
        MisColumn mainIdmisColumn = new MisColumn();
        mainIdmisColumn.setMisTypeId(mainTableId);
        mainIdmisColumn.setMisColumnId("id");
        mainIdmisColumn.setMisColumnLabel("id");
        mainIdmisColumn.setMisColumnName("id");
        mainIdmisColumn.setMisColumnType("1");
        columnList.add(mainIdmisColumn);
//        columnList.addAll(findByMisTypeId(mainTableId,"N"));
        currentColumnIds.addAll(columnList.stream().map(MisColumn::getMisColumnId).collect(Collectors.toList()));
        dto.getQfColumns().forEach(c -> {
            if(!CollectionUtil.contains(currentColumnIds,c.getMisQfcColumnId())){
                MisColumn byMisColumnId = columnDao.findByMisColumnId(c.getMisQfcColumnId());
                if(Objects.nonNull(byMisColumnId)){
                    columnList.add(byMisColumnId);
                }
                currentColumnIds.add(c.getMisQfcColumnId());
            }
        });

        tableId2conditions.put(mainTableId, dto.getQfConditions());
        if (CollectionUtil.isNotEmpty(dto.getCrossRef())) {
            dto.getCrossRef().forEach(cr -> {
//                columnList.addAll(findByMisTypeId(cr.getMisQfTableId(),"N"));
                currentColumnIds.addAll(columnList.stream().map(MisColumn::getMisColumnId).collect(Collectors.toList()));
                tableIds.add(cr.getMisQfTableId());
                cr.getQfColumns().forEach(c->{
                    if(!CollectionUtil.contains(currentColumnIds,c.getMisQfcColumnId())){
                        MisColumn byMisColumnId = columnDao.findByMisColumnId(c.getMisQfcColumnId());
                        if(Objects.nonNull(byMisColumnId)){
                            columnList.add(byMisColumnId);
                        }
                        currentColumnIds.add(c.getMisQfcColumnId());
                    }
                });
                tableId2conditions.put(cr.getMisQfTableId(), cr.getQfConditions());
            });
        }
        List<MisType> allTypes = typeDao.findAllById(tableIds);
        Map<String,MisType> tableId2misType = allTypes.stream().collect(Collectors.toMap(MisType::getMisTypeId,t -> t,(o,n)->n));
        Map<String,MisType> tableName2misType = allTypes.stream().collect(Collectors.toMap(MisType::getMisTypeName,t -> t,(o,n)->n));

        List<MisColumn> fullColumns = new ArrayList<>();
        Map<String,String> tableId2alias = new HashMap<>();int aliasIdx = 0;
        for (String tableId : tableIds) {
            tableId2alias.put(tableId,"t_" + aliasIdx++);
            fullColumns.addAll(columnDao.findByMisTypeId(tableId));
        }
        String mainTableAlias = tableId2alias.get(mainTableId);
        Map<String, MisColumn> columnId2misColumn = fullColumns.stream().collect(Collectors.toMap(MisColumn::getMisColumnId, c -> c, (o, n) -> n));
        Function<String, String> typeName2tableName = s -> s + "_s";

        Map<String, Integer> aliasDuplicateMap = new HashMap<>();
        for (MisColumn c : columnList) {
            String columnAlias = StrUtil.trim(c.getMisColumnLabel());
            //别名检查，sql语法不能存在两个相同别名的字段
            Integer duplicate = aliasDuplicateMap.compute(columnAlias, (k, v) -> Objects.isNull(v) ? 0 : v + 1);
            if (duplicate > 0) {
                columnAlias = columnAlias + "_" + duplicate;
            }
            c.setMisColumnLabel(columnAlias);
        }

        Map<String, String> columnId2tableId =
                columnList.stream().collect(Collectors.toMap(MisColumn::getMisColumnId, MisColumn::getMisTypeId, (o, n) -> n));

        //1.拼接select字段部分
        StringBuffer selectBuffer = new StringBuffer("select ");
        //增加返回列
        for (MisColumn c : columnList) {
            String tableAlias =
                    StrUtil.equals(mainTableId, c.getMisTypeId()) && StrUtil.equals("id", c.getMisColumnName())
                            ? mainTableAlias
                            : tableId2alias.get(columnId2tableId.get(c.getMisColumnId()));
            String columnName = c.getMisColumnName();
            String columnAlias = StrUtil.trim(c.getMisColumnLabel());
            selectBuffer.append(String.format("%s.%s as '%s'", tableAlias, columnName, columnAlias)).append(",");
        }
        //增加权限id列
        selectBuffer.append(mainTableAlias + ".mis_permission_id as mis_permission_id");

        //2.拼接from部分
        StringBuffer fromBuffer = new StringBuffer(" from ");
        //2.1 拼接主表部分: 示例 table_example t1alias
        fromBuffer.append(String.format("%s %s", typeName2tableName.apply(tableId2misType.get(mainTableId).getMisTypeName()), mainTableAlias));
        if (CollectionUtil.isNotEmpty(dto.getCrossRef())) {
            List<String> currentTableIds = new ArrayList<>();
            currentTableIds.add(mainTableId);
            dto.getCrossRef().forEach(cr -> {
                //找到从表与目前表集合的关联关系
                List<MisCrossRef> crossRefRelations = misCrossRefDao.findCrossRef(cr.getMisQfTableId(), currentTableIds);
                if (CollectionUtil.isEmpty(crossRefRelations)) {
                    throw new RuntimeException(String.format("no cross reference between [%s] and [%s]", cr.getMisQfTableId(), StrUtil.join(",", currentTableIds)));
                }
                currentTableIds.add(cr.getMisQfTableId());
                MisCrossRef crossRefRelation = crossRefRelations.get(0);
                MisType fromMisType = tableId2misType.get(crossRefRelation.getMisCrossRefParentTable());
                MisType toMisType = tableId2misType.get(crossRefRelation.getMisCrossRefChildTable());
                MisColumn fromColumn = columnId2misColumn.get(crossRefRelation.getMisCrossRefParentKey());
                MisColumn toColumn = columnId2misColumn.get(crossRefRelation.getMisCrossRefChildKey());
                MisType crMisType = tableId2misType.get(cr.getMisQfTableId());
                //根据关联关系拼接left join sql
                String crTableName = typeName2tableName.apply(crMisType.getMisTypeName());
                String crTableAlias = tableId2alias.get(cr.getMisQfTableId());
                String fromTableAlias = tableId2alias.get(fromMisType.getMisTypeId());
                String fromColumnName = fromColumn.getMisColumnName();
                String toTableAlias = tableId2alias.get(toMisType.getMisTypeId());
                String toColumnName = toColumn.getMisColumnName();
                fromBuffer.append(
                        String.format(
                                " left join %s %s on(%s.%s=%s.%s)",
                                crTableName, crTableAlias, fromTableAlias, fromColumnName, toTableAlias, toColumnName
                        )
                );
            });
        }
        //3.拼接where部分
        StringBuffer whereBuffer = new StringBuffer(" where 1=1 ");
        if (CollectionUtil.isNotEmpty(tableId2conditions)) {

            for (String tableId : tableId2conditions.keySet()) {
                List<MisQueryFormCondition> conditions = tableId2conditions.get(tableId);
                if (CollectionUtil.isEmpty(conditions)) {
                    continue;
                }
                whereBuffer.append(" and (");
                for (int j = 0, len = conditions.size(); j < len; j++) {
                    MisQueryFormCondition c = conditions.get(j);
                    MisColumn misColumn = columnId2misColumn.get(c.getMisQfc2ColumnId());
                    MisType misType = tableId2misType.get(misColumn.getMisTypeId());
                    ConditionEnum ce = ConditionEnum.getByCode(c.getMisQfc2Condition());
                    String tableAlias = tableId2alias.get(misType.getMisTypeId());
                    String columnName = misColumn.getMisColumnName();
                    String operator = ce.getOperator();
                    String value = ce.getPackValue().apply(c.getMisQfc2Value());
                    // %s %s.%s %s %s -> and t1.id = 'aaaa'
                    whereBuffer.append(
                            String.format(" %s %s.%s %s %s",
                                    j == 0 ? StrUtil.EMPTY : StrUtil.blankToDefault(c.getMisRelation(), "and"),
                                    tableAlias, columnName, operator, value));
                }
                whereBuffer.append(")");
            }
        }

        //3.1拼接folder_id条件
        String ids = folderService.getFolderChildList(dto.getFolderId());
        String[] folderArray = ids.split(",");
        StringBuilder folderStr = new StringBuilder("(");
        for (int k = 1; k < folderArray.length; k++) {
            if (k != folderArray.length - 1) {
                folderStr.append("'").append(folderArray[k]).append("',");
            } else {
                folderStr.append("'").append(folderArray[k]).append("'");
            }
        }
        folderStr.append(")");
        whereBuffer.append(String.format(" and %s.folder_id in %s", mainTableAlias, folderStr));

        //3.2拼接mis_permission_id条件
        whereBuffer.append(String.format(" and %s.mis_permission_id in (select mis_permission_id from mis_permission_detail mpd where mis_pd_right in('3','5','7'))",mainTableAlias));

        //3.3 拼接order by，limit语句
        PageStateDto pageState = dto.getPageState();
        int page = pageState.getPage();
        int pageSize =  pageState.getPageSize();
        String limit = String.format(" limit %d, %d",PageRequest.of(page, pageSize).getOffset(),pageSize);

        List<SortModelDto> sortModel = dto.getSortModel();
        StringBuilder orderByStr = new StringBuilder("");
        if(Objects.nonNull(sortModel) && sortModel.size() > 0){
            orderByStr.append(" order by ");
            for(int i=0,len=sortModel.size();i<len;i++){
                SortModelDto m = sortModel.get(i);
                String[] split = m.getField().split("\\.");//ks_ict_class_student.stu_gentle
                String tableAlias = tableId2alias.get(tableName2misType.get(split[0]).getMisTypeId());
                orderByStr.append(tableAlias + "." + split[1] + " " + m.getSort() + ",");
            }
            orderByStr.deleteCharAt(orderByStr.length() - 1);
        }

        String sql = StrUtil.join("", selectBuffer.toString(), fromBuffer.toString(), whereBuffer.toString(), orderByStr.toString(), limit);

        String countSql = StrUtil.join("", "SELECT COUNT(0)", fromBuffer.toString(), whereBuffer.toString());

        Query queryTotal = entityManager.createNativeQuery(countSql);
        long total = ((BigInteger) queryTotal.getResultList().get(0)).longValue();

        //4.根据权限过滤数据
        SearchRecordVO result = new SearchRecordVO();
        result.setTotal(total);
//        result.setRecordList(filterData(entityManager.createNativeQuery(sql).getResultList()));
        List resultList = entityManager.createNativeQuery(sql).getResultList();
        result.setRecordList(resultList);
        int j = 0;
        for (MisColumn c : columnList) {
            if (c.getMisColumnDictionary() != null) {
                List<DicDto> dicList = dictionaryService.getDicEnumAndSqlListByDicId(c.getMisColumnDictionary());
                for(int num=0; num<resultList.size(); num++){
                    Object[] object = (Object[]) resultList.get(num);
                    for (DicDto dicItem : dicList) {
                        if (dicItem.getKey().equals(String.valueOf(object[j]))) {
                            object[j] = dicItem.getValue();
                            break;
                        }
                    }
                }
            }
            c.setMisColumnName(tableId2misType.get(c.getMisTypeId()).getMisTypeName() + "." + c.getMisColumnName());//前端组件的column field 需要是唯一的
            j ++;
        }
        result.setColumnList(columnList);
        calcComputeValue(columnList,resultList);
        return result;
    }

    @Override
    public List<MisColumn> getColumnList(SearchRecordDTO dto) {
        List<String> tableIds = new ArrayList<>();
        List<String> columnIds = new ArrayList<>();
        List<MisColumn> fullColumns = new ArrayList<>();
        Map<String, MisColumn> columnId2misColumn = new HashMap<>();
        List<MisColumn> result = new ArrayList<>();
        String mainTableId = dto.getTypeId();
        tableIds.add(mainTableId);
        dto.getQfColumns().forEach(c -> {
            columnIds.add(c.getMisQfcColumnId());
        });
        if (CollectionUtil.isNotEmpty(dto.getCrossRef())) {
            dto.getCrossRef().forEach(cr -> {
                tableIds.add(cr.getMisQfTableId());
                cr.getQfColumns().forEach(c -> {
                    columnIds.add(c.getMisQfcColumnId());
                });
            });
        }
        for (String tableId : tableIds) {
            List<MisColumn> tableColumns = columnDao.findByMisTypeId(tableId);
            fullColumns.addAll(tableColumns);
        }
        columnId2misColumn = fullColumns.stream().collect(Collectors.toMap(MisColumn::getMisColumnId, c -> c, (o, n) -> n));

        MisColumn misColumn = new MisColumn();
        misColumn.setMisTypeId(mainTableId);
        misColumn.setMisColumnLabel("id");
        misColumn.setMisColumnType("1");
        result.add(misColumn);

        Map<String, Integer> aliasDuplicateMap = new HashMap<>();
        int i = 0;
        for (String cid : columnIds) {
            MisColumn c = columnId2misColumn.get(cid);
            String columnAlias = StrUtil.trim(c.getMisColumnLabel());
            //别名检查，sql语法不能存在两个相同别名的字段
            Integer duplicate = aliasDuplicateMap.compute(columnAlias, (k, v) -> Objects.isNull(v) ? 0 : v + 1);
            if (duplicate > 0) {
                columnAlias = columnAlias + "_" + duplicate;
            }
            c.setMisColumnLabel(columnAlias);
            c.setMisColumnName(c.getMisColumnName() + i++);//前端组件的column field 需要是唯一的
            result.add(c);
        }

        return result;
    }

    @Override
    public List getRecords(String typeId, String columns, String dateFrom, String dateTo) {
        MisType type = typeDao.findById(typeId).get();
        dateFrom = dateFrom.substring(0, 10) + " 00:00:00";
        dateTo = dateTo.substring(0, 10) + " 23:59:59";
        Query query = entityManager.createNativeQuery("select " + columns + " from " + type.getMisTypeName() + "_s" + " where 1=1 and creation_date >='" + dateFrom + "' and creation_date<='" + dateTo + "'");
        List list = query.getResultList();
        return list;
    }

    @Override
    public List<MisColumn> findByMisTypeId(String misTypeId) {
        return columnDao.findByMisTypeId(misTypeId);
    }

    @Override
    public List<MisColumn> findByMisTypeId(String misTypeId, String allowSearch) {
        Specification<MisColumn> specification =
                Specifications.<MisColumn>and()
                        .eq("misTypeId", misTypeId)
                        .eq(StrUtil.isNotBlank(allowSearch), "misColumnAllowSearch", allowSearch)
                        .build();
        return columnDao.findAll(specification);
    }

    @Override
    public PageDataDto getFolderRecordByPage(String typeId,String folderId,List<MisColumn> misColumnList, JSONArray sortModel, JSONObject pageState) {
        String column = "";
//        String dic = "";
        int page = pageState.optInt("page");
        int pageSize =  pageState.optInt("pageSize");
        String limit = String.format(" limit %d, %d",PageRequest.of(page, pageSize).getOffset(),pageSize);
        Map<Integer,List<DicDto>> pos2dicList = new HashMap<>();
        for (int i = 1; i < misColumnList.size(); i++) {
            if (i != misColumnList.size() - 1) {
                column += misColumnList.get(i).getMisColumnName() + ",";
            } else {
                column += misColumnList.get(i).getMisColumnName();
            }
            if(StringUtils.isNotEmpty(misColumnList.get(i).getMisColumnDictionary())){
                pos2dicList.put(i,dictionaryService.getDicEnumAndSqlListByDicId(misColumnList.get(i).getMisColumnDictionary()));
//                dic+=i+"&"+misColumnList.get(i).getMisColumnDictionary()+",";
            }
        }
        column = "id," + column;
        if (StrUtil.endWith(column, ',')) {
            column = column.substring(0, column.length() - 1);
        }
        MisType type = typeDao.findById(typeId).get();

        String ids = folderService.getFolderChildList(folderId);
        String[] folderArray = ids.split(",");
        StringBuilder folderStr = new StringBuilder("(");
        for (int i = 1; i < folderArray.length; i++) {
            if (i != folderArray.length - 1) {
                folderStr.append("'").append(folderArray[i]).append("',");
            } else {
                folderStr.append("'").append(folderArray[i]).append("'");
            }
        }
        folderStr.append(")");
        StringBuilder orderByStr = new StringBuilder("");
        if(Objects.nonNull(sortModel) && sortModel.length() > 0){
            orderByStr.append(" order by ");
            for(int i = 0,len = sortModel.length();i<len;i++){
                try {
                    JSONObject jsonObject = sortModel.getJSONObject(i);
                    orderByStr.append(jsonObject.getString("field") + " " + jsonObject.getString("sort") + ",");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            orderByStr.deleteCharAt(orderByStr.length() - 1);
        }

        /*StringBuilder querySQL = new StringBuilder("SELECT "+column+" FROM "+type.getMisTypeName()+"_s  where " +
                "folder_id in "+folderStr + orderByStr + limit);*/
        String userId=ContextHolder.getUserId();
        StringBuilder querySQL=null;
        List<MisGpTypePermission> misGpTypePermissionList=misGpPermissionDao.findByMisTypeIdAndCreateBy(typeId,userId);
        if(misGpTypePermissionList.size()>0) {
            querySQL = new StringBuilder("SELECT " + column + " FROM " + type.getMisTypeName() + ConstUtils.TYPEDRAFT+"  where " +
                    "folder_id in " + folderStr+ orderByStr + limit);
        }else {
            querySQL = new StringBuilder("SELECT " + column + " FROM " + type.getMisTypeName() + ConstUtils.TYPEMAIN + "  where " +
                    "folder_id in " + folderStr + orderByStr + limit);
        }

        String queryTotalSQL = String.format("SELECT COUNT(0) FROM %s WHERE folder_id in %s",type.getMisTypeName()+"_s",folderStr);
        Query queryTotal = entityManager.createNativeQuery(queryTotalSQL);
        long total = ((BigInteger) queryTotal.getResultList().get(0)).longValue();

        Query query = entityManager.createNativeQuery(querySQL.toString());
        List list = query.getResultList();
        List<Object[]> result = new ArrayList<>();
//        List res = new ArrayList();
        PageDataDto pageDataDto = new PageDataDto();
        pageDataDto.setTotal(total);
        pageDataDto.setData(result);
        for(int i = 0,len=list.size();i<len;i++){
            Object o = list.get(i);
            Object[] object = o instanceof Object[] ? (Object[])o : new Object[]{o};
//            Object[] object = (Object[]) list.get(i);
            pos2dicList.forEach((pos,dicList) -> {
                Optional<DicDto> first =
                    dicList.stream()
                        .filter(dic -> StrUtil.equals(dic.getKey(), StrUtil.nullToEmpty(object[pos])))
                        .findFirst();
                if(first.isPresent()){
                    object[pos] = first.get().getValue();
                }
            });
            result.add(object);
        }
        calcComputeValue(misColumnList,result);
        return pageDataDto;
    }

    @Override
    public ResultGenerator getDefaultRecordList(RecordListPageableDto dto){

        List<MisColumn> misColumnList = new ArrayList<>();
        MisColumn misColumn = new MisColumn();
        misColumn.setMisTypeId(dto.getTypeId());
        misColumn.setMisColumnName("id");
        misColumn.setMisColumnLabel("Id");
        misColumn.setMisColumnType("1");
        misColumnList.add(misColumn);
        List<MisColumn> columnList  = findByMisTypeId(dto.getTypeId());
        if(columnList.size()==0){
            return new ResultGenerator(300,"No columns in table.");
        }

        List<TypeIdDto> propertyList  = widgetColumnDao.getDefaultColumnsByWidgetId(dto.getWidgetId());
        for (int i = 0; i < columnList.size(); i++) {
            TypeIdDto columnIdDto = new TypeIdDto();
            columnIdDto.setId(columnList.get(i).getMisColumnId());
            if(propertyList.contains(columnIdDto)) {
                misColumnList.add(columnList.get(i));
            }
        }

        PageDataDto pageData = getRecordsByPage(dto, misColumnList);
        Map<String,Object> map = new HashMap<>();
        map.put("columnList", misColumnList);
        map.put("recordList", pageData.getData());
        map.put("total", pageData.getTotal());
        return ResultGenerator.getSuccessResult(map);
    }

    private void calcComputeValue(List<MisColumn> columnList,List<Object[]> dataList){
        boolean mulTable = columnList.stream().map(c -> c.getMisTypeId()).distinct().count() > 1;
        Map<String,MisColumn> id2column =
                columnList.stream().collect(Collectors.toMap(MisColumn::getMisColumnId,t -> t,(o,n)->n));
        Map<Integer,MisColumn> pos2column = new HashMap<>();
        Map<Integer,MisColumn> pos2calcColumn = new HashMap<>();//需要计算值的字段
        Map<String,Integer> id2pos = new HashMap<>();

        for(int i=0,len=columnList.size();i<len;i++){
            MisColumn c = columnList.get(i);
            if(StrUtil.containsAny(c.getMisColumnInputType(),
//                    TableMgmtColumnInputType.COMPUTE_FIELD.getCode(),
//                    TableMgmtColumnInputType.QUERY_TEXT_BOX.getCode(),
                    TableMgmtColumnInputType.REPEATING_FIELD.getCode()
            )){
                pos2calcColumn.put(i,c);
            }
            pos2column.put(i,c);
            id2pos.put(c.getMisColumnId(),i);
        }
        //便利返回数据,计算值
        for(int i = 0,len=dataList.size();i<len;i++){
            Object o = dataList.get(i);
            Object[] object = o instanceof Object[] ? (Object[])o : new Object[]{o};
//            Object[] object = (Object[]) dataList.get(i);
            pos2calcColumn.forEach((pos,c)->{
                String misColumnComputeFrom = c.getMisColumnComputeFrom();
                boolean fromFormula =
                    StrUtil.equals(c.getMisColumnInputType(),TableMgmtColumnInputType.COMPUTE_FIELD.getCode())
                            && "1".equals(misColumnComputeFrom);
                //1:Formula
                if(fromFormula){
                    MisColumnComputeFormulaVO formula = JSON.parseObject(c.getMisColumnComputeFormula(),MisColumnComputeFormulaVO.class);
                    Object val1 = object[id2pos.get(formula.getMisColumnId1())];
                    Object val2 = object[id2pos.get(formula.getMisColumnId2())];
                    String operator = formula.getOperator();
                    MisColumn val1Column = id2column.get(formula.getMisColumnId1());
                    TableMgmtColumnType columnType = TableMgmtColumnType.getByCode(val1Column.getMisColumnType());
                    try{
                        object[pos] =
                                MisColumnComputeFormulaVO.calc(
                                        columnType,val1,operator,val2
                                );
                    }
                    catch (DefinitionException e){
                        object[pos] = e.getErrorMsg();
                    }
                    catch (Exception e){
                        object[pos] = String.format("Calculation failed,columnType:%s,val1:%s,op:%s,val2:%s",columnType,val1,operator,val2);
                    }
                }
            });

            //先计算Formula再计算Query,Query中可以使用到Formula的结果进行查询
            pos2calcColumn.forEach((pos,c)-> {
                String misColumnComputeFrom = c.getMisColumnComputeFrom();
                boolean fromQuery =
                    (StrUtil.equals(c.getMisColumnInputType(),TableMgmtColumnInputType.COMPUTE_FIELD.getCode())
                            && "2".equals(misColumnComputeFrom)) ||
                    (StrUtil.equals(c.getMisColumnInputType(),TableMgmtColumnInputType.QUERY_TEXT_BOX.getCode()));
                Map<String,Object> columnName2Value = new HashMap<>();
                pos2column.forEach((k,v)->{
                    if(StrUtil.equals(c.getMisTypeId(),v.getMisTypeId())){
                        String columnName = v.getMisColumnName();
                        columnName = columnName.substring(columnName.indexOf(StrUtil.C_DOT) + 1);
                        columnName2Value.put(columnName,object[k]);
                    }
                });
                if(fromQuery){
                    String computeQuerySql = StrUtil.format(c.getMisColumnComputeQuery(),"$value(",")",columnName2Value);
                    Query computeQuery = entityManager.createNativeQuery(computeQuerySql);
                    List computeQueryList = computeQuery.getResultList();
                    if(CollectionUtil.isNotEmpty(computeQueryList)){
                        object[pos] = computeQueryList.get(0);
                    }
                }
            });

            pos2calcColumn.forEach((pos,c)-> {
                if(TableMgmtColumnInputType.REPEATING_FIELD.getCode().equals(c.getMisColumnInputType())){
                    Map<String,Object> columnName2Value = new HashMap<>();
                    pos2column.forEach((k,v)->{
                        if(StrUtil.equals(c.getMisTypeId(),v.getMisTypeId())){
                            String columnName = v.getMisColumnName();
                            columnName = columnName.substring(columnName.indexOf(StrUtil.C_DOT) + 1);
                            columnName2Value.put(columnName,object[k]);
                        }
                    });
                    TypeListDto misType = typeDao.findTypeByMisTypeId(c.getMisTypeId());
                    String sqlFormat = "select value from %s_r where id = '%s' and mis_column_id = '%s'";
                    String sql = sqlFormat.formatted(misType.getMisTypeName(), object[0], c.getMisColumnId());
                    Query sqlQuery = entityManager.createNativeQuery(sql);
                    List computeQueryList = sqlQuery.getResultList();
                    if(CollectionUtil.isNotEmpty(computeQueryList)){
                        object[pos] = JSON.toJSONString(computeQueryList);
                    }
                }
            });
        }
    }

    @Override
    public List<ColumnInputTypeDTO> getPropertiesLabel(String tableId) {
        StringBuilder querySQL = new StringBuilder("select CONCAT(mc.mis_column_label,'=',mc.mis_column_name,'=',mc.mis_column_input_type,'=',mc.mis_column_dictionary) from mis_property_config mpc,mis_property_config_detail mpcd,mis_column mc \n" +
                "where mpc.mis_property_id = mpcd.mis_property_id " +
                "and mpcd.mis_property_config_detail_column_id = mc.mis_column_id " +
                "and mpc.mis_property_table_id = '" + tableId + "'");
        Query query = entityManager.createNativeQuery(querySQL.toString());
        List list = query.getResultList();
        List<ColumnInputTypeDTO> inputTypeDTOList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String column = (String) list.get(i);
            ColumnInputTypeDTO inputTypeDTO = new ColumnInputTypeDTO();
            inputTypeDTO.setColumnName(column);
            if ("2".equals(column.split("=")[2]) || "4".equals(column.split("=")[2])) {
                inputTypeDTO.setDictList(dictionaryDao.getDicListById(column.split("=")[3]));
            }
            inputTypeDTOList.add(inputTypeDTO);
        }
        return inputTypeDTOList;
    }

    public List<String> getPropertiesName(String tableId) {
        StringBuilder querySQL = new StringBuilder("select mc.mis_column_name from mis_property_config mpc,mis_property_config_detail mpcd,mis_column mc \n" +
                "where mpc.mis_property_id = mpcd.mis_property_id " +
                "and mpcd.mis_property_config_detail_column_id = mc.mis_column_id " +
                "and mpc.mis_property_table_id = '" + tableId + "'");
        Query query = entityManager.createNativeQuery(querySQL.toString());
        List list = query.getResultList();
        return list;
    }

    @Override
    public List<ColumnInputDTO> getProperties(String tableId,String recordId) throws Exception {

        // 1. get property column
        StringBuilder querySQL = new StringBuilder(
                " select mc.mis_column_label, mc.mis_column_name, mc.mis_column_type,  " +
                        " mc.mis_column_input_type, mc.mis_column_dictionary, " +
                        " mpcd.row_size, mpcd.col_size, mc.mis_column_width, mc.mis_column_length, mc.mis_column_id, " +
                        " mpcd.mis_property_config_detail_id from mis_column mc " +
                        " inner join mis_property_config_detail mpcd on mc.mis_column_id = mpcd.mis_property_config_detail_column_id " +
                        " where mis_type_id = '" + tableId + "'"
        );

        Query query = entityManager.createNativeQuery(querySQL.toString());
        List<Object[]> list = query.getResultList();

        List<ColumnInputDTO> inputTypeDTOList = new ArrayList<>();
        List valueList = getPropertiesValue(tableId,recordId);
        MisType type = typeDao.findById(tableId).get();
        String userId = ContextHolder.getUserId();

        try{
            for (int i = 0; i < list.size(); i++) {
                Object[] column = list.get(i);

                // 2. Judge column permission
                List<MisPropertyColumnPermission> columnPermissions =
                        misPropertyColumnPermissionDao.findByMisPropertyConfigDetailColumnId((String)column[10]);
                // 2.1. is invisible
                Boolean invisible = false;
                for(MisPropertyColumnPermission columnPermission : columnPermissions){
                    // 2.1.1 judge user in group
                    if(columnPermission.getMisPdType().equals("3")){
                        String childIds = memberDao.queryChildNodeIds(columnPermission.getMisPdPerformerId());
                        if(null != childIds){
                            if(childIds.contains(userId) && columnPermission.getMisPdAction().equals("I")){
                                invisible = true;
                                break;
                            }
                        }
                    }
                    // 2.1.2 judge is current user
                    if(columnPermission.getMisPdType().equals("4")){
                        if(userId.equals(columnPermission.getMisPdPerformerId()) && columnPermission.getMisPdAction().equals("I")){
                            invisible = true;
                            break;
                        }
                    }
                }
                if(invisible){continue;}

                ColumnInputDTO inputTypeDTO = new ColumnInputDTO();

                // 2.2. is disabled
                for(MisPropertyColumnPermission columnPermission : columnPermissions) {
                    // 2.2.1 judge user in group
                    if (columnPermission.getMisPdType().equals("3")) {
                        String childIds = memberDao.queryChildNodeIds(columnPermission.getMisPdPerformerId());
                        if (null != childIds) {
                            if (childIds.contains(userId) && columnPermission.getMisPdAction().equals("D")) {
                                inputTypeDTO.setDisabled(true);
                                break;
                            }
                        }
                    }
                    // 2.2.2 judge is current user
                    if (columnPermission.getMisPdType().equals("4")) {
                        if (userId.equals(columnPermission.getMisPdPerformerId()) && columnPermission.getMisPdAction().equals("D")) {
                            inputTypeDTO.setDisabled(true);
                            break;
                        }
                    }
                }

                inputTypeDTO.setMisColumnLabel((String)column[0]);
                inputTypeDTO.setMisColumnName((String)column[1]);
                inputTypeDTO.setMisColumnType((String)column[2]);
                inputTypeDTO.setMisColumnInputType((String)column[3]);
                inputTypeDTO.setMisColumnDictionary((String)column[4]);
                if("2".equals((String)column[3]) || "4".equals((String)column[3])){
                    inputTypeDTO.setDictList(dictionaryService.getDicEnumAndSqlListByDicId((String)column[4]));
                }
                inputTypeDTO.setRow_size((Integer)column[5]);
                inputTypeDTO.setCol_size((Integer)column[6]);
                inputTypeDTO.setMisColumnWidth(Float.valueOf((String) column[7]));
                inputTypeDTO.setMisColumnLength((String)column[8]);
                inputTypeDTO.setMisColumnId((String)column[9]);
                if(!valueList.isEmpty() && valueList.get(0).getClass().isArray()){
                    Object[] object = (Object[]) valueList.get(0);
                    inputTypeDTO.setValue(object[i]);
                    if(TableMgmtColumnInputType.REPEATING_FIELD.getCode().equals(inputTypeDTO.getMisColumnInputType())){
                        String sqlFormat = "select value from %s_r where id = '%s' and mis_column_id = '%s'";
                        String sql = sqlFormat.formatted(type.getMisTypeName(), recordId, inputTypeDTO.getMisColumnId());
                        Query sqlQuery = entityManager.createNativeQuery(sql);
                        List computeQueryList = sqlQuery.getResultList();
                        if(CollectionUtil.isNotEmpty(computeQueryList)){
                            inputTypeDTO.setValue(JSON.toJSONString(computeQueryList));
                        }
                    }
                }else {
                    if(!valueList.isEmpty()) {
                        inputTypeDTO.setValue(valueList.get(0));
                    }
                }
                inputTypeDTOList.add(inputTypeDTO);
            }

            Map<String,ColumnInputDTO> columnName2dto =
                inputTypeDTOList.stream().collect(Collectors.toMap(o-> o.getMisColumnName().toLowerCase(), o->o,(o, n)->n));

            for(int i=0,len=inputTypeDTOList.size();i<len;i++){
                ColumnInputDTO inputTypeDTO = inputTypeDTOList.get(i);
                if(TableMgmtColumnInputType.REPEATING_FIELD.getCode().equals(inputTypeDTO.getMisColumnInputType()) ||
                        TableMgmtColumnInputType.QUERY_TEXT_BOX.getCode().equals(inputTypeDTO.getMisColumnInputType())){
                    MisColumn referenceById = misColumnDao.findByMisColumnId(inputTypeDTO.getMisColumnId());
                    String sql = referenceById.getMisColumnComputeQuery();
                    Pattern pattern = Pattern.compile("\\$value\\((.*?)\\)");
                    Matcher matcher = pattern.matcher(sql);
                    List<String> dependColumnNames = new ArrayList<>();
                    while (matcher.find()) {
                        ColumnInputDTO columnInputDTO = columnName2dto.get(matcher.group(1).toLowerCase());
                        if(Objects.nonNull(columnInputDTO)){
                            dependColumnNames.add(columnInputDTO.getMisColumnName());
                        }
                    }
                    if(CollectionUtil.isNotEmpty(dependColumnNames)){
                        EffectColumn effectColumn = new EffectColumn();
                        effectColumn.setFrom(StrUtil.join(",",dependColumnNames));
                        effectColumn.setTarget(referenceById.getMisColumnName());
                        for(String columnName:dependColumnNames){
                            ColumnInputDTO columnInputDTO = columnName2dto.get(columnName.toLowerCase());
                            if(Objects.nonNull(columnInputDTO)){
                                if(Objects.isNull(columnInputDTO.getEffect())){
                                    columnInputDTO.setEffect(new ArrayList<>());
                                }
                                columnInputDTO.getEffect().add(effectColumn);
                            }
                        }
                    }
                    //sql不为空,且不依赖其他列的 直接获取sql的查询值
                    if(StrUtil.isNotBlank(sql) && CollectionUtil.isEmpty(dependColumnNames)){
                        CalcColumnQueryResultVO calcColumnQueryResultVO = recordMange.calcColumnQueryResult(inputTypeDTO.getMisColumnId(), null);
                        List<String> result = calcColumnQueryResultVO.getResult();
                        inputTypeDTO.setDictList(result.stream().map(str -> new DicDto(str,str)).collect(Collectors.toList()));
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return inputTypeDTOList;
    }

    @Override
    public List<ColumnInputDTO> getRefProperties(String tableId, String recordId, String misCrossRefId) throws Exception {

        TypeRefDto typeRefDto = misCrossRefDao.findMisCrossRefByMisCrossRefId(misCrossRefId);

        StringBuilder querySQL = new StringBuilder(
                " select mc.mis_column_label, mc.mis_column_name, mc.mis_column_type,  " +
                        " mc.mis_column_input_type, mc.mis_column_dictionary, " +
                        " mpcd.row_size, mpcd.col_size, mc.mis_column_width, mc.mis_column_length, mc.mis_column_id from mis_column mc " +
                        " inner join mis_property_config_detail mpcd on mis_column_id = mpcd.mis_property_config_detail_column_id " +
                        " where mis_type_id = '" + typeRefDto.getMisCrossRefChildTableID() + "'"
        );

        Query query = entityManager.createNativeQuery(querySQL.toString());
        List<Object[]> list = query.getResultList();
        List<ColumnInputDTO> inputTypeDTOList = new ArrayList<>();

        List valueList = getRefPropertiesValue(typeRefDto, recordId);

        try {
            for (int i = 0; i < list.size(); i++) {
                Object[] column = list.get(i);
                ColumnInputDTO inputTypeDTO = new ColumnInputDTO();
                inputTypeDTO.setMisColumnLabel((String) column[0]);
                inputTypeDTO.setMisColumnName((String) column[1]);
                inputTypeDTO.setMisColumnType((String) column[2]);
                inputTypeDTO.setMisColumnInputType((String) column[3]);
                inputTypeDTO.setMisColumnDictionary((String) column[4]);
                if ("2".equals((String) column[3]) || "4".equals((String) column[3])) {
                    inputTypeDTO.setDictList(dictionaryService.getDicEnumAndSqlListByDicId((String) column[4]));
                }
                inputTypeDTO.setRow_size((Integer)column[5]);
                inputTypeDTO.setCol_size((Integer)column[6]);
                inputTypeDTO.setMisColumnWidth((Float) column[7]);
                inputTypeDTO.setMisColumnLength((String)column[8]);
                inputTypeDTO.setMisColumnId((String)column[9]);


                if (valueList.get(0).getClass().isArray()) {
                    Object[] object = (Object[]) valueList.get(0);
                    inputTypeDTO.setValue(object[i]);
                } else {
                    inputTypeDTO.setValue(valueList.get(0));
                }
                inputTypeDTOList.add(inputTypeDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputTypeDTOList;
    }

    @Override
    public String getRefRecordId(TypeRefDto typeRefDto, String recordId) {

        StringBuilder querySQL = new StringBuilder("SELECT ");
        List<String> columns = getPropertiesName(typeRefDto.getMisCrossRefChildTableID());

        MisColumn parentColumn = columnDao.findByMisColumnId(typeRefDto.getMisCrossRefParentKey());
        MisColumn childrenColumn = columnDao.findByMisColumnId(typeRefDto.getMisCrossRefChildKey());

        String parentTableName = typeRefDto.getMisCrossRefParentTableName() + "_s";
        String childrenTableName = typeRefDto.getMisCrossRefChildTableName() + "_s";

        querySQL.append(childrenTableName + ".id ");

        MisType type = typeDao.findById(typeRefDto.getMisCrossRefChildTableID()).get();
        querySQL.append(" FROM " + childrenTableName + " left join " + parentTableName + " on " + childrenTableName + "." + childrenColumn.getMisColumnName()
                + "=" + parentTableName + "." + parentColumn.getMisColumnName() + " where 1=1 and " + parentTableName + ".id= '" + recordId + "'");
        Query query = entityManager.createNativeQuery(querySQL.toString());
        List list = query.getResultList();
        return (String) list.get(0);
    }


    @Override
    public List getPropertiesValue(String tableId, String id) {

        StringBuilder querySQL = new StringBuilder("SELECT ");
        List<String> columns = getPropertiesName(tableId);
        for (int i = 0; i < columns.size(); i++) {
            if (i != columns.size() - 1) {
                querySQL.append(columns.get(i) + ",");
            } else {
                querySQL.append(columns.get(i));
            }
        }
        MisType type = typeDao.findById(tableId).get();
        querySQL.append(" FROM " + type.getMisTypeName() + "_s where 1=1 and id='" + id + "'");
        Query query = entityManager.createNativeQuery(querySQL.toString());
        List list = query.getResultList();
        return list;
    }

    @Override
    public Boolean isEdit(String tableId, String id) {
        StringBuilder querySQL = new StringBuilder("SELECT *");
        MisType type = typeDao.findById(tableId).get();
        querySQL.append(" FROM " + type.getMisTypeName() + "_s where 1=1 and id='" + id + "'");
        Query query = entityManager.createNativeQuery(querySQL.toString());
        List list = query.getResultList();
        String userId = ContextHolder.getUserId();
        if (null != list) {
            Object[] object = (Object[]) list.get(0);
            List<MisPermissionDetail> detailList = permissionDetailDao.getListByPermissionId((String) object[6]);
            for (int j = 0; j < detailList.size(); j++) {
                if ("5".equals(detailList.get(j).getMisPdRight()) || "7".equals(detailList.get(j).getMisPdRight())) {
                    if ("4".equals(detailList.get(j).getMisPdType()) && userId.equals(detailList.get(j).getMisPdPerformerId())) {
                        return true;
                    }
                    if ("3".equals(detailList.get(j).getMisPdType())) {
                        String childIds = memberDao.queryChildNodeIds(detailList.get(j).getMisPdPerformerId());
                        if (null != childIds) {
                            if (childIds.contains(userId)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Boolean isDelete(String tableId, String id) {
        StringBuilder querySQL = new StringBuilder("SELECT *");
        MisType type = typeDao.findById(tableId).get();
        querySQL.append(" FROM " + type.getMisTypeName() + "_s where 1=1 and id='" + id + "'");
        Query query = entityManager.createNativeQuery(querySQL.toString());
        List list = query.getResultList();
        String userId = ContextHolder.getUserId();
        if (null != list) {
            Object[] object = (Object[]) list.get(0);
            List<MisPermissionDetail> detailList = permissionDetailDao.getListByPermissionId((String) object[6]);
            for (int j = 0; j < detailList.size(); j++) {
                if ("7".equals(detailList.get(j).getMisPdRight())) {
                    if ("4".equals(detailList.get(j).getMisPdType()) && userId.equals(detailList.get(j).getMisPdPerformerId())) {
                        return true;
                    }
                    if ("3".equals(detailList.get(j).getMisPdType())) {
                        String childIds = memberDao.queryChildNodeIds(detailList.get(j).getMisPdPerformerId());
                        if (null != childIds) {
                            if (childIds.contains(userId)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Boolean hasChildrenTable(String tableId) {

        List<TypeRefPropertyDto> list = misCrossRefDao.findAllTypeRef(tableId);
        Boolean hasChildren = false;
        for (int i = 0; i < list.size(); i++) {
            TypeRefPropertyDto typeRefPropertyDto = list.get(i);
            if (typeRefPropertyDto.getMisCrossRefParentTableID().equals(tableId)) {
                hasChildren = true;
            }
        }
        return hasChildren;
    }


    @Override
    @Transactional
    public Boolean saveProperties(String id, String typeId, JSONObject arr) throws JSONException {
        MisType type = typeDao.findById(typeId).get();
        List<MisColumn> columns = misColumnDao.findByMisTypeId(typeId);
        Map<String,MisColumn> id2column = columns.stream().collect(Collectors.toMap(o-> o.getMisColumnId(), o->o,(o, n)->n));
        List<String> sqls = new ArrayList<>();

        Map<String,String> column2value = new HashMap<>();
        List<ColumnInputDTO> inputList = new ArrayList<>();
        for(Iterator<?> iter = arr.keys(); iter.hasNext();){
            String key = (String) iter.next();
            JSONObject getValue=new JSONObject(arr.getString(key));
            String value = getValue.getString("value");
            column2value.put(key.toLowerCase(),value);
            String inputType = getValue.getString("input_type");
            ColumnInputDTO dto = new ColumnInputDTO();
            dto.setMisColumnName(key);
            dto.setValue(value);
            dto.setMisColumnInputType(inputType);
            inputList.add(dto);
        }

        for(ColumnInputDTO input:inputList){
            //computeField
            if(TableMgmtColumnInputType.COMPUTE_FIELD.getCode().equals(input.getMisColumnInputType())){
                List<MisColumn> byMisTypeIdAndMisColumnName = misColumnDao.findByMisTypeIdAndMisColumnName(typeId, input.getMisColumnName());
                MisColumn column = null;
                if(CollectionUtil.isNotEmpty(byMisTypeIdAndMisColumnName)){
                    column = byMisTypeIdAndMisColumnName.get(0);
                    String misColumnComputeFrom = column.getMisColumnComputeFrom();
                    boolean fromFormula = "1".equals(misColumnComputeFrom);
                    boolean fromQuery = "2".equals(misColumnComputeFrom);
                    Object result = "";
                    if(fromFormula){
                        try{
                            MisColumnComputeFormulaVO formula = JSON.parseObject(column.getMisColumnComputeFormula(),MisColumnComputeFormulaVO.class);
                            MisColumn column1 = id2column.get(formula.getMisColumnId1());
                            MisColumn column2 = id2column.get(formula.getMisColumnId2());
                            Object val1 = column2value.get(column1.getMisColumnName().toLowerCase());
                            Object val2 = column2value.get(column2.getMisColumnName().toLowerCase());
                            TableMgmtColumnType columnType = TableMgmtColumnType.getByCode(column1.getMisColumnType());
                            String operator = formula.getOperator();
                            result = MisColumnComputeFormulaVO.calc(columnType,val1,operator,val2);
                            column2value.put(column.getMisColumnName().toLowerCase(),StrUtil.nullToEmpty(result));
                        }
                        catch (Exception e){
                            LOGGER.error("error during compute from formula,param:" + column2value,e);
                        }
                    }
                    if(fromQuery){
                        try{
                            String computeQuerySql = StrUtil.format(column.getMisColumnComputeQuery(),"$value(",")",column2value);
                            Query computeQuery = entityManager.createNativeQuery(computeQuerySql);
                            List computeQueryList = computeQuery.getResultList();
                            if(CollectionUtil.isNotEmpty(computeQueryList)){
                                result = computeQueryList.get(0);
                                column2value.put(column.getMisColumnName().toLowerCase(),StrUtil.nullToEmpty(result));
                            }
                        }
                        catch (Exception e){
                            LOGGER.error("error during compute from sql,param:" + column2value,e);
                        }
                    }
                }
            }
            //repeatField
            if(TableMgmtColumnInputType.REPEATING_FIELD.getCode().equals(input.getMisColumnInputType())){
                List<String> split = com.alibaba.fastjson.JSONArray.parseArray(StrUtil.nullToEmpty(input.getValue()), String.class);
                split = CollectionUtil.sort(split,String::compareTo);
                List<MisColumn> repeatingColumnList = misColumnDao.findByMisTypeIdAndMisColumnName(typeId, input.getMisColumnName());
                if(CollectionUtil.isNotEmpty(repeatingColumnList)){
                    MisColumn misColumn = repeatingColumnList.get(0);
                    String tableName = type.getMisTypeName() + "_r";
                    String deleteBeforeFormat = "delete from %s where id='%s' and mis_column_id = %s";
                    sqls.add(String.format(deleteBeforeFormat,tableName,id,misColumn.getMisColumnId()));
                    String sqlFormat = "insert into %s(id,mis_column_id,value) values ('%s','%s','%s')";

                    for(String v:split){
                        sqls.add(String.format(sqlFormat,tableName,id,misColumn.getMisColumnId(),v));
                    }
                }
                column2value.put(input.getMisColumnName().toLowerCase(),StrUtil.nullToEmpty(split.get(0)));
            }
        }

        StringBuilder dataArrBuf = new StringBuilder();
        column2value.forEach((k,v) -> {
            dataArrBuf.append(""+ k + "=" +"'"+v+"'"+",");
        });
        dataArrBuf.deleteCharAt(dataArrBuf.length() - 1);

        String updateSql= "update "+type.getMisTypeName()+"_s set " + dataArrBuf.toString() + " where id= '"+id+"'";
        sqls.add(updateSql);
        if(CollectionUtil.isNotEmpty(sqls)){
            for(String sql:sqls){
                Query query = entityManager.createNativeQuery(sql);
                query.executeUpdate();
            }
        }
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteProperties(String id, String typeId) {
        MisType type = typeDao.findById(typeId).get();
        StringBuilder querySQL = new StringBuilder("delete from " + type.getMisTypeName() + "_s ");
        querySQL.append(" where id= '" + id + "'");
        Query query = entityManager.createNativeQuery(querySQL.toString());
        query.executeUpdate();
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteRefProperties(String id, String typeId) {

        List<TypeRefPropertyDto> list = misCrossRefDao.findAllTypeRef(id);
        for (int i = 0; i < list.size(); i++) {
            TypeRefPropertyDto typeRefPropertyDto = list.get(i);
            if (typeRefPropertyDto.getMisCrossRefParentTableID().equals(id)) {
                TypeRefDto typeRefDto = misCrossRefDao.findMisCrossRefByMisCrossRefId(typeRefPropertyDto.getMisCrossRefId());
                MisColumn parentColumn = columnDao.findByMisColumnId(typeRefDto.getMisCrossRefParentKey());
                MisColumn childrenColumn = columnDao.findByMisColumnId(typeRefDto.getMisCrossRefChildKey());

                String parentTableName = typeRefDto.getMisCrossRefParentTableName() + "_s";
                String childrenTableName = typeRefDto.getMisCrossRefChildTableName() + "_s";

                StringBuilder querySQL = new StringBuilder(" delete from " + childrenTableName + " left join " + parentTableName + " on " +
                        " on " + childrenTableName + "." + childrenColumn + " = " + parentTableName + "." + parentColumn);
                querySQL.append(" where " + parentTableName + ".id= '" + id + "'");
                Query query = entityManager.createNativeQuery(querySQL.toString());
                query.executeUpdate();
            }
        }
        return true;
    }


    @Override
    @Transactional
    public void importExcelData(StringBuffer appendSql) {
        Query query = entityManager.createNativeQuery(appendSql.toString());
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void delImportTtable(String tableName) {
        Query query = entityManager.createNativeQuery("delete from " + tableName);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void insertImportConfig(MisImportHist misImportHist) {
        misImportHistDao.save(misImportHist);
    }

    @Override
    public Boolean isSubscribe(String recordId) {
        StringBuilder querySQL = new StringBuilder("select 1 from mis_subscription where mis_subscription_obj_id ='" + recordId + "'");
        Query query = entityManager.createNativeQuery(querySQL.toString());
        List list = query.getResultList();
        if (list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List getRefPropertiesValue(TypeRefDto typeRefDto, String recordId) {

        StringBuilder querySQL = new StringBuilder("SELECT ");
        List<String> columns = getPropertiesName(typeRefDto.getMisCrossRefChildTableID());

        MisColumn parentColumn = columnDao.findByMisColumnId(typeRefDto.getMisCrossRefParentKey());
        MisColumn childrenColumn = columnDao.findByMisColumnId(typeRefDto.getMisCrossRefChildKey());

        String parentTableName = typeRefDto.getMisCrossRefParentTableName()  +"_s";
        String childrenTableName = typeRefDto.getMisCrossRefChildTableName()  +"_s";
        if(CollectionUtil.isNotEmpty(columns)){
            for (int i = 0; i < columns.size(); i++) {
                if(i!= columns.size()-1){
                    querySQL.append( childrenTableName +"."+ columns.get(i)+",");
                }else{
                    querySQL.append( childrenTableName +"."+ columns.get(i));
                }
            }
        }
        else{
            querySQL.append( childrenTableName +".*");
        }


        MisType type = typeDao.findById(typeRefDto.getMisCrossRefChildTableID()).get();
        querySQL.append(" FROM " + childrenTableName + " left join " + parentTableName + " on " + childrenTableName + "." + childrenColumn.getMisColumnName()
                + "=" + parentTableName + "." + parentColumn.getMisColumnName() + " where 1=1 and " + parentTableName + ".id= '" + recordId + "'");
        Query query = entityManager.createNativeQuery(querySQL.toString());
        List list = query.getResultList();
        return list;
    }


    private String getCondition(String condition) {
        String str = "";
        if ("0".equals(condition)) {
            str = "like";
        } else if ("1".equals(condition)) {
            str = "=";
        } else if ("2".equals(condition)) {
            str = "<=";
        } else if ("3".equals(condition)) {
            str = ">=";
        }
        return str;
    }

    @Override
    public Map<String, Object> getRecordListByRecIds(RecordIdListDto dto) {

        Map<String, Object> map = new HashMap<>();
        String typeId = dto.getTypeId();
        List<MisColumn> misColumnList = new ArrayList<>();
        MisColumn misColumn = new MisColumn();
        misColumn.setMisTypeId(typeId);
        misColumn.setMisColumnLabel("id");
        misColumn.setMisColumnType("1");
        misColumnList.add(misColumn);

        List<MisColumn> columnList = findByMisTypeId(typeId);
        if (columnList.size() == 0) {
            map.put("error", new ResultGenerator(300, "No columns in table."));
            return map;
        }

        List<TypeIdDto> propertyList = misPropertyConfigDetailDao.getMisPropertyConfigDetailsByMisTypeId(typeId);
        for (int i = 0; i < columnList.size(); i++) {
            TypeIdDto columnIdDto = new TypeIdDto();
            columnIdDto.setId(columnList.get(i).getMisColumnId());
            if (propertyList.contains(columnIdDto)) {
                misColumnList.add(columnList.get(i));
            }
        }

        String column = "";
        for (int i = 1; i < misColumnList.size(); i++) {
            if (i != misColumnList.size() - 1) {
                column += misColumnList.get(i).getMisColumnName() + ",";
            } else {
                column += misColumnList.get(i).getMisColumnName();
            }
        }

        column = "id," + column;
        MisType type = typeDao.findById(typeId).get();
        String recordIds = "";
        for (int i = 1; i <= dto.getRecordIdList().size(); i++) {
            if (i == dto.getRecordIdList().size()) {
                recordIds = recordIds + "'" + dto.getRecordIdList().get(i - 1) + "'";
            } else {
                recordIds = recordIds + "'" + dto.getRecordIdList().get(i - 1) + "',";
            }
        }
        StringBuilder querySQL =null;
        List<MisGpTypePermission> misGpTypePermissionList=misGpPermissionDao.findByMisTypeId(typeId);
        if(misGpTypePermissionList.size()>0){
            querySQL = new StringBuilder("SELECT " + column + " FROM " + type.getMisTypeName() + ConstUtils.TYPEDRAFT+"  where id in (" + recordIds + ")");
        }else{
            querySQL = new StringBuilder("SELECT " + column + " FROM " + type.getMisTypeName() +ConstUtils.TYPEMAIN+"  where id in (" + recordIds + ")");
        }
        Query query = entityManager.createNativeQuery(querySQL.toString());
        List recordList = query.getResultList();
        map.put("columnList", misColumnList);
        map.put("recordList", recordList);
        return map;
    }

    @Override
    public ResultGenerator getRecordHistoryByRecId(RecordIdDto dto) {

        Map<String, Object> map = new HashMap<>();
        String typeId = dto.getTypeId();
        if (typeId == null) {
            return new ResultGenerator(300, "No columns in table.");
        }
        List<MisColumn> misColumnList = new ArrayList<>();
        MisColumn misColumn0 = new MisColumn();
        misColumn0.setMisTypeId(typeId);
        misColumn0.setMisColumnName("uid");
        misColumn0.setMisColumnLabel("uid");
        misColumn0.setMisColumnType("1");
        misColumnList.add(misColumn0);
        MisColumn misColumn = new MisColumn();
        misColumn.setMisTypeId(typeId);
        misColumn.setMisColumnName("transactionDate");
        misColumn.setMisColumnLabel("Transaction Date");
        misColumn.setMisColumnType("4");
        misColumnList.add(misColumn);
        MisColumn misColumn1 = new MisColumn();
        misColumn1.setMisTypeId(typeId);
        misColumn1.setMisColumnName("action");
        misColumn1.setMisColumnLabel("Action");
        misColumn1.setMisColumnType("1");
        misColumnList.add(misColumn1);

        MisType type = typeDao.findById(typeId).get();
        StringBuilder querySQL = new StringBuilder("SELECT uid,transaction_date,action FROM " + type.getMisTypeName() + "_s_log  where id in (" + dto.getRecordId() + ")");
        Query query = entityManager.createNativeQuery(querySQL.toString());
        List recordList = query.getResultList();
        map.put("columnList", misColumnList);
        map.put("recordList", recordList);
        return ResultGenerator.getSuccessResult(map);
    }


    @Override
    public ResultGenerator getRecordAuditDetailByRecId(RecordIdDto dto) {

        Map<String, Object> map = new HashMap<>();
        String typeId = dto.getTypeId();
        if (typeId == null) {
            return new ResultGenerator(300, "No columns in table.");
        }
        List<MisColumn> misColumnList = new ArrayList<>();
        RecordAuditDetailDto recordAuditDetailDto = new RecordAuditDetailDto();
        Field[] fields = recordAuditDetailDto.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            MisColumn misColumn0 = new MisColumn();
            misColumn0.setMisTypeId(typeId);
            misColumn0.setMisColumnName(fieldName);
            misColumn0.setMisColumnLabel(fieldName);
            if(field.getType().getName().equals("Date")){
                misColumn0.setMisColumnType("4");
            }else{
                misColumn0.setMisColumnType("1");
            }
            misColumnList.add(misColumn0);
            System.out.println(fieldName);
            System.out.println("field.getType().getName()"+field.getType().getName());
        }

        List recordList = auditDetailDao.getMisAuditDetailByRecordId(dto.getRecordId());
        map.put("columnList", misColumnList);
        map.put("recordList", recordList);
        return ResultGenerator.getSuccessResult(map);
    }

    @Override
    public ResultGenerator getRecordComparisonByRecId(RecordComparisonDto dto) {
        Map<String, Object> map = new HashMap<>();
        String typeId = dto.getTypeId();
        if (typeId == null) {
            return new ResultGenerator(300, "No columns in table.");
        }
        List<MisColumn> misColumnList = new ArrayList<>();
        List<MisColumn> cols = new ArrayList<>();
        MisColumn misColumn = new MisColumn();
        misColumn.setMisTypeId(typeId);
        misColumn.setMisColumnName("columnName");
        misColumn.setMisColumnLabel("Column Name");
        misColumn.setMisColumnType("1");
        cols.add(misColumn);


        StringBuilder querySQL = new StringBuilder();
        List<MisColumn> columnList = findByMisTypeId(typeId);
        if (columnList.size() == 0) {
            return new ResultGenerator(300, "No columns in table.");
        }

        List<TypeIdDto> propertyList = misPropertyConfigDetailDao.getMisPropertyConfigDetailsByMisTypeId(typeId);
        for (int i = 0; i < columnList.size(); i++) {
            TypeIdDto columnIdDto = new TypeIdDto();
            columnIdDto.setId(columnList.get(i).getMisColumnId());
            if (propertyList.contains(columnIdDto)) {
                misColumnList.add(columnList.get(i));
            }
        }
        MisType type = typeDao.findById(typeId).get();
        String sql = "SELECT COUNT(id) from " + type.getMisTypeName() + "_s_log where uid in ('" + dto.getId1() + "','" + dto.getId2() + "') GROUP BY id";
//        String sql = "SELECT COUNT(id) from " + type.getMisTypeName() + "_s_log where uid in ('1','3') GROUP BY id";
        List<BigInteger> count = entityManager.createNativeQuery(sql).getResultList();
        Integer valueCount = count.get(0).intValue();

        for (int k = 1; k <= 2; k++) {
            MisColumn misColumn1 = new MisColumn();
            misColumn1.setMisTypeId(typeId);
            misColumn1.setMisColumnName("value" + k);
            misColumn1.setMisColumnLabel("Value" + k);
            misColumn1.setMisColumnType("1");
            cols.add(misColumn1);
        }





        StringBuilder colsStr = new StringBuilder();
        for (MisColumn c : misColumnList) {
            colsStr.append(c.getMisColumnName() + ",");
        }
        colsStr.deleteCharAt(colsStr.length() - 1);
        List result = entityManager.createNativeQuery("select " + colsStr + " from " + type.getMisTypeName() + "_s_log where uid in ('" + dto.getId1() + "','" + dto.getId2() + "') ").getResultList();
//        List result = entityManager.createNativeQuery("select " + colsStr + " from " + type.getMisTypeName() + "_s_log where uid in ('1','3') ").getResultList();


        if(valueCount == 1){
            for (int j = 0; j < misColumnList.size(); j++) {
                if (j == 0) {
                    for (int i = 0; i < 2; i++) {
                        if (i == 0) {
                            querySQL.append("SELECT '" + misColumnList.get(j).getMisColumnName() + "' as 'Column Name',' ' AS value1");
                        } else if (i == 1) {
                            querySQL.append(",`" + misColumnList.get(j).getMisColumnName() + "` AS value2");
                        }
                    }
                    querySQL.append(" from " + type.getMisTypeName() + "_s_log where uid in ('" + dto.getId1() + "','" + dto.getId2() + "') ");
                } else {
                    querySQL.append(" UNION ");
                    for (int i = 0; i < 2; i++) {
                        if (i == 0) {
                            querySQL.append("SELECT '" + misColumnList.get(j).getMisColumnName() + "' as 'Column Name',' ' AS value1");
                        } else if (i == 1) {
                            querySQL.append(",`" + misColumnList.get(j).getMisColumnName() + "` AS value2");
                        }
                    }
                    querySQL.append(" from " + type.getMisTypeName() + "_s_log where uid in ('" + dto.getId1() + "','" + dto.getId2() + "') ");
                }
            }
        }else{
            List<MisColumn> comparisonList = new ArrayList<>();
            if(misColumnList.size() == 1){
                if("1,3,4".contains(misColumnList.get(0).getMisColumnType())){
                    if(!(result.get(0)).equals(result.get(1))){
                        comparisonList.add(misColumnList.get(0));
                    }
                }else if("0,2".contains(misColumnList.get(0).getMisColumnType())){
                    if(!(result.get(0)).equals(result.get(1))){
                        comparisonList.add(misColumnList.get(0));
                    }
                }
            }else if(misColumnList.size() > 1){
                System.out.println(result.get(0));
                for(int t = 0;t<misColumnList.size();t++){
                    if("1,3,4".contains(misColumnList.get(t).getMisColumnType())){
                        if(!((((Object[])result.get(0))[t])).equals(((Object[])result.get(1))[t])){
                            comparisonList.add(misColumnList.get(t));
                        }
                    }else if("0,2".contains(misColumnList.get(t).getMisColumnType())){
                        if((result.get(0)).equals(result.get(1))){
                            comparisonList.add(misColumnList.get(t));
                        }
                    }
                }

            }
            for (int j = 0; j < comparisonList.size(); j++) {

                if (j == 0) {
                    for (int i = 0; i < valueCount; i++) {
                        int temp = i + 1;
                        if (i == 0) {
                            querySQL.append("SELECT '" + comparisonList.get(j).getMisColumnName() + "' as 'Column Name',SUBSTRING_INDEX(GROUP_CONCAT(`" + comparisonList.get(j).getMisColumnName() + "` ORDER BY transaction_date), ',', 1) AS value1");
                        } else if (i == valueCount - 1) {
                            querySQL.append(",SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(`" + comparisonList.get(j).getMisColumnName() + "` ORDER BY transaction_date), ',', " + temp + "), ',', -1) AS value" + temp);
                        } else if (i != valueCount - 1) {
                            querySQL.append(",SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(`" + comparisonList.get(j).getMisColumnName() + "` ORDER BY transaction_date), ',', " + temp + "), ',', -1) AS value" + temp);
                        }
                    }
                    querySQL.append(" from " + type.getMisTypeName() + "_s_log where uid in ('" + dto.getId1() + "','" + dto.getId2() + "') GROUP BY id");
//                querySQL.append(" from " + type.getMisTypeName() + "_s_log where uid in ('1','3') GROUP BY id");
                } else {
                    querySQL.append(" UNION ");
                    for (int i = 0; i < valueCount; i++) {
                        int temp = i + 1;
                        if (i == 0) {
                            querySQL.append("SELECT '" + comparisonList.get(j).getMisColumnName() + "' as 'Column Name',SUBSTRING_INDEX(GROUP_CONCAT(`" + comparisonList.get(j).getMisColumnName() + "` ORDER BY transaction_date), ',', 1) AS value1");
                        } else if (i == valueCount - 1) {
                            querySQL.append(",SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(`" + comparisonList.get(j).getMisColumnName() + "` ORDER BY transaction_date), ',', " + temp + "), ',', -1) AS value" + temp);
                        } else if (i != valueCount - 1) {
                            querySQL.append(",SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(`" + comparisonList.get(j).getMisColumnName() + "` ORDER BY transaction_date), ',', " + temp + "), ',', -1) AS value" + temp);
                        }
                    }
                    querySQL.append(" from " + type.getMisTypeName() + "_s_log where uid in ('" + dto.getId1() + "','" + dto.getId2() + "') GROUP BY id");
//                querySQL.append(" from " + type.getMisTypeName() + "_s_log where uid in ('1','3') GROUP BY id");
                }


            }
        }

//        StringBuilder querySQL = new StringBuilder("SELECT transaction_date,action FROM "+type.getMisTypeName()+"_s_log  where id in ("+ dto.getRecordId() +")");
        Query query = entityManager.createNativeQuery(querySQL.toString());
        List recordList = query.getResultList();
        map.put("columnList", cols);
        map.put("recordList", recordList);
        return ResultGenerator.getSuccessResult(map);
    }
    @Override
    public ResultGenerator getRecordEditListByRecIds(RecordIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        String typeId = dto.getTypeId();
        if (typeId == null) {
            return new ResultGenerator(300, "No columns in table.");
        }
        List<MisColumn> misColumnList = new ArrayList<>();
        StringBuffer stringBuffer=new StringBuffer();
        MisColumn misColumn0 = new MisColumn();
        misColumn0.setMisTypeId(typeId);
        misColumn0.setMisColumnName("uid");
        misColumn0.setMisColumnLabel("uid");
        misColumn0.setMisColumnType("1");
        misColumnList.add(misColumn0);
        List<MisColumn> getColumnLs=columnDao.findByMisTypeId(typeId);
        if(getColumnLs.size()>0){
            for(int i=0;i<getColumnLs.size();i++){
                MisColumn misColumn = new MisColumn();
                misColumn.setMisTypeId(typeId);
                misColumn.setMisColumnName(getColumnLs.get(i).getMisColumnName());
                misColumn.setMisColumnLabel(getColumnLs.get(i).getMisColumnLabel());
                misColumn.setMisColumnType(getColumnLs.get(i).getMisColumnType());
                misColumnList.add(misColumn);
                stringBuffer.append(getColumnLs.get(i).getMisColumnName()+" ,");
            }
        }

        String replaceColumn=!stringBuffer.toString().isEmpty()?stringBuffer.toString().substring(0,stringBuffer.length()-1):stringBuffer.toString();
        MisType type = typeDao.findById(typeId).get();
        StringBuilder querySQL = new StringBuilder("SELECT uid ,"+replaceColumn+ " FROM " + type.getMisTypeName() + "_s_log  where id in (" + dto.getRecordId() + ")");
        Query query = entityManager.createNativeQuery(querySQL.toString());
        List recordList = query.getResultList();
        map.put("columnList", misColumnList);
        map.put("recordList", recordList);
        return ResultGenerator.getSuccessResult(map);
    }

    private PageDataDto getRecordsByPage(RecordListPageableDto dto,List<MisColumn> misColumnList) {
        String column = "";
//        String dic = "";
        int page = dto.getPageState().getPage();
        int pageSize = dto.getPageState().getPageSize();
        String limit = String.format(" limit %d, %d",PageRequest.of(page, pageSize).getOffset(),pageSize);
        Map<Integer,List<DicDto>> pos2dicList = new HashMap<>();
        for (int i = 1; i < misColumnList.size(); i++) {
            if (i != misColumnList.size() - 1) {
                column += misColumnList.get(i).getMisColumnName() + ",";
            } else {
                column += misColumnList.get(i).getMisColumnName();
            }
            if(StringUtils.isNotEmpty(misColumnList.get(i).getMisColumnDictionary())){
                pos2dicList.put(i,dictionaryService.getDicEnumAndSqlListByDicId(misColumnList.get(i).getMisColumnDictionary()));
//                dic+=i+"&"+misColumnList.get(i).getMisColumnDictionary()+",";
            }
        }
        column= "id,"+column;
        if(StrUtil.endWith(column,',')){
            column = column.substring(0,column.length() - 1);
        }
        MisType type = typeDao.findById(dto.getTypeId()).get();

        String ids = folderService.getFolderChildList(dto.getFolderId());
        String[] folderArray  = ids.split(",");
        StringBuilder folderStr = new StringBuilder("(");
        for (int i = 1; i < folderArray.length; i++) {
            if(i!=folderArray.length-1){
                folderStr.append("'").append(folderArray[i]).append("',");
            }else{
                folderStr.append("'").append(folderArray[i]).append("'");
            }
        }
        folderStr.append(")");
        StringBuilder orderByStr = new StringBuilder();
        if(null != dto.getSortModel() && dto.getSortModel().size() > 0){
            orderByStr.append(" order by ");
            for(int i = 0,len = dto.getSortModel().size();i<len;i++){
                SortModelDto sort = dto.getSortModel().get(i);
                orderByStr.append(sort.getField() + " " + sort.getSort() + ",");
            }
            orderByStr.deleteCharAt(orderByStr.length() - 1);
        }

        StringBuilder querySQL = new StringBuilder("SELECT "+column+" FROM "+type.getMisTypeName()+"_s  where " +
                "folder_id in "+folderStr + orderByStr + limit);

        String queryTotalSQL = String.format("SELECT COUNT(0) FROM %s WHERE folder_id in %s",type.getMisTypeName()+"_s",folderStr);
        Query queryTotal = entityManager.createNativeQuery(queryTotalSQL);
        long total = ((BigInteger) queryTotal.getResultList().get(0)).longValue();

        Query query = entityManager.createNativeQuery(querySQL.toString());
        List list = query.getResultList();
        List<Object[]> result = new ArrayList<>();
//        List res = new ArrayList();
        PageDataDto pageDataDto = new PageDataDto();
        pageDataDto.setTotal(total);
        pageDataDto.setData(result);
        for(int i = 0,len=list.size();i<len;i++){
            Object o = list.get(i);
            Object[] object = o instanceof Object[] ? (Object[])o : new Object[]{o};
//            Object[] object = (Object[]) list.get(i);
            pos2dicList.forEach((pos,dicList) -> {
                Optional<DicDto> first =
                        dicList.stream()
                                .filter(dic -> StrUtil.equals(dic.getKey(), StrUtil.nullToEmpty(object[pos])))
                                .findFirst();
                if(first.isPresent()){
                    object[pos] = first.get().getValue();
                }
            });
            result.add(object);
        }
        calcComputeValue(misColumnList,result);
        return pageDataDto;
    }

}

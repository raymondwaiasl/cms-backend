package com.asl.prd004.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.asl.prd004.dao.FolderDao;
import com.asl.prd004.dao.GroupDao;
import com.asl.prd004.dao.MisPermissionDao;
import com.asl.prd004.dao.MisPermissionDetailDao;
import com.asl.prd004.dao.MisTypeDao;
import com.asl.prd004.dao.MisUserDao;
import com.asl.prd004.dto.MisPermissionDto;
import com.asl.prd004.dto.PermissionDTO;
import com.asl.prd004.entity.MisFolder;
import com.asl.prd004.entity.MisGroup;
import com.asl.prd004.entity.MisPermission;
import com.asl.prd004.entity.MisPermissionDetail;
import com.asl.prd004.entity.MisType;
import com.asl.prd004.entity.MisUser;
import com.asl.prd004.service.IPermissionService;
import com.asl.prd004.utils.SerialNumberUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PermissionService implements IPermissionService {

    @Autowired
    private MisPermissionDao misPermissionDao;

    @Autowired
    private MisPermissionDetailDao misPermissionDetailDao;

    @Autowired
    private FolderDao folderDao;

    @Autowired
    private MisPermissionDao permissionDao;

    @Autowired
    private MisTypeDao typeDao;

    @Autowired
    EntityManager entityManager;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private MisPermissionDetailDao permissionDetailDao;
    @Autowired
    private MisUserDao userDao;

    @Override
    public List<Object[]> getAllPermission(String typeName, String permName) {
        List<Object[]> misPermissionLs = misPermissionDao.getAllPermission(typeName, permName);
        return misPermissionLs;
    }

    @Override
    @Transactional
    public int insertMisPermission(String misPermissionId, String perMissionName, String folderPer) {
        MisPermission misPermission = new MisPermission();
        misPermission.setMisPermissionId(misPermissionId);
        misPermission.setMisPermissionName(perMissionName);
        misPermission.setMisPermissionType(folderPer);
        misPermissionDao.save(misPermission);
        return 1;
    }

    @Override
    @Transactional
    public int insertPermDetail(String misPerDetailId, String misPermissionId, String childType, String misPdPerformerId, String rightData) {
        MisPermissionDetail misPermissionDetail = new MisPermissionDetail();
        misPermissionDetail.setMisPdId(misPerDetailId);
        misPermissionDetail.setMisPermissionId(misPermissionId);
        misPermissionDetail.setMisPdType(childType);
        misPermissionDetail.setMisPdPerformerId(misPdPerformerId);
        misPermissionDetail.setMisPdRight(rightData);
        misPermissionDetailDao.save(misPermissionDetail);
        return 1;
    }

    @Override
    @Transactional
    public int delPermission(String permissionId) {
        misPermissionDao.delPermissionById(permissionId);
        return 1;
    }

    @Override
    @Transactional
    public int delPermissionValue(String permissionId) {
        misPermissionDetailDao.delPermissionValueById(permissionId);
        return 1;
    }

    @Override
    public String queryMisPermissionId(String perMissionName, String folderPer) {
        String misPermissionId = misPermissionDao.queryMisPermissionId(perMissionName, folderPer);
        return misPermissionId;
    }

    @Override
    public String queryMisPermissionDetailId(String misPermissionId, String childType, String s, String rightData) {
        String misPermissionDetailId = misPermissionDetailDao.queryMisPermissionDetailId(misPermissionId, childType, s, rightData);
        return misPermissionDetailId;
    }

    @Override
    @Transactional
    @Modifying
    public boolean savePermission(PermissionDTO permissionDTO) {

        MisPermission permission = null;
        MisPermission temp = permissionDTO.getPermission();
        MisPermission p = new MisPermission();
        p.setMisPermissionId("");
        p.setMisPermissionName("");
        p.setMisPermissionType("");
        permission = permissionDao.save(p);
        List<MisPermissionDetail> details = new ArrayList<>();
        for (MisPermissionDetail d : temp.getDetails()) {
            d.setMisPdId(null);
            d.setMisPermissionId(permission.getMisPermissionId());
            details.add(d);
        }
        permission.setDetails(details);
        permission = permissionDao.saveAndFlush(permission);

        if(permissionDTO.getFolderId().contains("_")){
            String[] ids = permissionDTO.getFolderId().split("_");
            String typeId = "";
            String recordId = "";
            if (ids.length > 0) {
                typeId = ids[0];
                recordId = ids[1];
            }
            Optional<MisType> optional = typeDao.findById(typeId);
            MisType type = new MisType();
            if(optional.isPresent()){
                type = optional.get();
            }
            String sql = "update " + type.getMisTypeName() + "_s set mis_permission_id = '" + permission.getMisPermissionId() + "'  WHERE id = '" + recordId + "'";
            int res = entityManager.createNativeQuery(sql).executeUpdate();
            if(res == 1){
                return true;
            }
        }else{
            String foldersStr = folderDao.getFolderChildList(permissionDTO.getFolderId());
            if ("2".equals(permissionDTO.getFolderPer())) {
                String[] folders = foldersStr.split(",");
                if (folders.length > 0 && null != permission) {
                    for (int i = 0; i < folders.length; i++) {
                        if (!"".equals(folders[i])) {
                            MisFolder folder = folderDao.findMisFolderByMisFolderId(folders[i]);
                            if (null != folder) {
                                folder.setMisPermissionId(permission.getMisPermissionId());
                                folderDao.saveAndFlush(folder);
                            }
                        }
                    }
                    return true;
                }
            } else {
                if (null != permission) {
                    MisFolder folder = folderDao.findMisFolderByMisFolderId(permissionDTO.getFolderId());
//            MisFolder folder = folderDao.findMisFolderByMisFolderId("0015000000000055");
                    if (null != folder) {
                        folder.setMisPermissionId(permission.getMisPermissionId());
                        folderDao.saveAndFlush(folder);
                        return true;
                    }
                }
            }
        }


        return false;
    }

    @Override
    @Transactional
    public void deletePermissionByUserId(String misUserId) {
        misPermissionDetailDao.deletePermissionByUserId(misUserId);
    }

    @Override
    @Transactional
    public String createPermission(String misPermissionName, String detail) {
        JSONArray jsonArray = JSONArray.parseArray(detail);

        for (int i = 0; i < jsonArray.size(); i++) {
            com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) jsonArray.get(i);
            String misPdType = jsonObject.getString("MIS_PD_TYPE");
            String misPerformerName = jsonObject.getString("MIS_PERFORMER_NAME");
            String misPdRight = jsonObject.getString("MIS_PD_RIGHT");
            if ("Group".equals(misPdType)) {
                List<MisGroup> groupList = groupDao.findByMisGroupName(misPerformerName);
                if(groupList.size() == 0){
                    return "-3";
                }
            } else if ("User".equals(misPdType)) {
                List<MisUser> userList = userDao.findByMisUserName(misPerformerName);
                if(userList.size() == 0){
                    return "-3";
                }
            }
            if("READ".equals(misPdRight) || "WRITE".equals(misPdRight) || "DELETE".equals(misPdRight)){

            }else{
                return "-4";
            }
        }

        MisPermission permission = new MisPermission();
        permission.setMisPermissionName(misPermissionName);
        permission.setMisPermissionType("");
        permissionDao.saveAndFlush(permission);
        for (int i = 0; i < jsonArray.size(); i++) {
            com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) jsonArray.get(i);
            String misPdType = jsonObject.getString("MIS_PD_TYPE");
            String misPerformerName = jsonObject.getString("MIS_PERFORMER_NAME");
            String misPdRight = jsonObject.getString("MIS_PD_RIGHT");
            String rdRight = "";
            if ("READ".equals(misPdRight)) {
                rdRight = "3";
            } else if ("WRITE".equals(misPdRight)) {
                rdRight = "5";
            } else if ("DELETE".equals(misPdRight)) {
                rdRight = "7";
            }
            if ("Group".equals(misPdType)) {
                List<MisGroup> groupList = groupDao.findByMisGroupName(misPerformerName);
                for (MisGroup group : groupList) {
                    MisPermissionDetail permissionDetail = new MisPermissionDetail();
                    permissionDetail.setMisPdType("3");
                    permissionDetail.setMisPdPerformerId(group.getMisGroupId());
                    permissionDetail.setMisPdRight(rdRight);
                    permissionDetail.setMisPermissionId(permission.getMisPermissionId());
                    permissionDetailDao.save(permissionDetail);
                }
            } else if ("User".equals(misPdType)) {
                List<MisUser> userList = userDao.findByMisUserName(misPerformerName);
                for (MisUser user : userList) {
                    MisPermissionDetail permissionDetail = new MisPermissionDetail();
                    permissionDetail.setMisPdType("4");
                    permissionDetail.setMisPdPerformerId(user.getMisUserId());
                    permissionDetail.setMisPdRight(rdRight);
                    permissionDetail.setMisPermissionId(permission.getMisPermissionId());
                    permissionDetailDao.save(permissionDetail);
                }
            }
        }
        return permission.getMisPermissionId();
    }

    @Override
    @Transactional
    public String updatePermission(String misPermissionId, String detail) {
        Optional<MisPermission> option = permissionDao.findById(misPermissionId);
        MisPermission permission = null;
        if(option.isPresent()){
            permission = option.get();
        }else{
            return "-5";
        }
        JSONArray jsonArray = JSONArray.parseArray(detail);
        for (int i = 0; i < jsonArray.size(); i++) {
            com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) jsonArray.get(i);
            String misPdType = jsonObject.getString("MIS_PD_TYPE");
            String misPerformerName = jsonObject.getString("MIS_PERFORMER_NAME");
            String misPdRight = jsonObject.getString("MIS_PD_RIGHT");
            if ("Group".equals(misPdType)) {
                List<MisGroup> groupList = groupDao.findByMisGroupName(misPerformerName);
                if(groupList.size() == 0){
                    return "-3";
                }
            } else if ("User".equals(misPdType)) {
                List<MisUser> userList = userDao.findByMisUserName(misPerformerName);
                if(userList.size() == 0){
                    return "-3";
                }
            }
            if("READ".equals(misPdRight) || "WRITE".equals(misPdRight) || "DELETE".equals(misPdRight)){

            }else{
                return "-4";
            }
        }
        //先删除detail
        permissionDetailDao.delPermissionValueById(misPermissionId);

        for (int i = 0; i < jsonArray.size(); i++) {
            com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) jsonArray.get(i);
            String misPdType = jsonObject.getString("MIS_PD_TYPE");
            String misPerformerName = jsonObject.getString("MIS_PERFORMER_NAME");
            String misPdRight = jsonObject.getString("MIS_PD_RIGHT");
            String rdRight = "";
            if ("READ".equals(misPdRight)) {
                rdRight = "3";
            } else if ("WRITE".equals(misPdRight)) {
                rdRight = "5";
            } else if ("DELETE".equals(misPdRight)) {
                rdRight = "7";
            }
            if ("Group".equals(misPdType)) {
                List<MisGroup> groupList = groupDao.findByMisGroupName(misPerformerName);
                for (MisGroup group : groupList) {
                    MisPermissionDetail permissionDetail = new MisPermissionDetail();
                    permissionDetail.setMisPdType("3");
                    permissionDetail.setMisPdPerformerId(group.getMisGroupId());
                    permissionDetail.setMisPdRight(rdRight);
                    permissionDetail.setMisPermissionId(permission.getMisPermissionId());
                    permissionDetailDao.save(permissionDetail);
                }
            } else if ("User".equals(misPdType)) {
                List<MisUser> userList = userDao.findByMisUserName(misPerformerName);
                for (MisUser user : userList) {
                    MisPermissionDetail permissionDetail = new MisPermissionDetail();
                    permissionDetail.setMisPdType("4");
                    permissionDetail.setMisPdPerformerId(user.getMisUserId());
                    permissionDetail.setMisPdRight(rdRight);
                    permissionDetail.setMisPermissionId(permission.getMisPermissionId());
                    permissionDetailDao.save(permissionDetail);
                }
            }
        }
        return permission.getMisPermissionId();
    }
    @Override
    @Transactional
    public String deletePermission(String misPermissionId) {
        Optional<MisPermission> option = permissionDao.findById(misPermissionId);
        if(!option.isPresent()){
            return "-5";
        }
        permissionDao.deleteById(misPermissionId);

        permissionDetailDao.delPermissionValueById(misPermissionId);

        return "0";
    }


    @Override
    public MisPermissionDto queryPermissionByRecordId(String typeId, String recordId) {
        MisPermissionDto permission = null;

        Optional<MisType> optional = typeDao.findById(typeId);
        MisType type = new MisType();
        if(optional.isPresent()){
            type = optional.get();
        }

        String sql = "select  mis_permission_id  FROM " + type.getMisTypeName() + "_s WHERE id = '" + recordId + "'";
        List<String> res = entityManager.createNativeQuery(sql).getResultList();
        for (String s : res) {
            if (null != s) {
                MisPermission p = permissionDao.findMisPermissionByMisPermissionId(s);
                MisPermissionDto dto = new MisPermissionDto();
                dto.setMisPermissionId(p.getMisPermissionId());
                dto.setMisPermissionName(p.getMisPermissionName());
                dto.setMisPermissionType(p.getMisPermissionType());
                List<MisPermissionDetail> detailList = new ArrayList<>();
                for(MisPermissionDetail d: p.getDetails()){
                    d.setPermission(null);
                    detailList.add(d);
                }
                dto.setDetails(detailList);
                return dto;
            }
        }
        return permission;
    }


    @Override
    public MisPermissionDto queryPermissionByFolderId(String folderId) {
        MisPermissionDto permission = null;
        MisFolder folder = folderDao.findMisFolderByMisFolderId(folderId);

//        List<MisPermission> list = permissionDao.findAll();
//        System.out.println(list);
        if (null != folder) {
            MisPermission p = permissionDao.findMisPermissionByMisPermissionId(folder.getMisPermissionId());
            MisPermissionDto dto = new MisPermissionDto();
            dto.setMisPermissionId(p.getMisPermissionId());
            dto.setMisPermissionName(p.getMisPermissionName());
            dto.setMisPermissionType(p.getMisPermissionType());
            List<MisPermissionDetail> detailList = new ArrayList<>();
            for(MisPermissionDetail d: p.getDetails()){
                d.setPermission(null);
                detailList.add(d);
            }
            dto.setDetails(detailList);
            return dto;
        }
        return permission;
    }

    @Transactional
    @Override
    public String appendMember(String misPermissionId, String detail) {
        Optional<MisPermission> option = permissionDao.findById(misPermissionId);
        MisPermission permission = null;
        if(option.isPresent()){
            permission = option.get();
        }else{
            return "-5";
        }
        JSONArray jsonArray = JSONArray.parseArray(detail);
        for (int i = 0; i < jsonArray.size(); i++) {
            com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) jsonArray.get(i);
            String misPdType = jsonObject.getString("MIS_PD_TYPE");
            String misPerformerName = null;
            String misPdRight = jsonObject.getString("MIS_PD_RIGHT");
            if ("Group".equals(misPdType)) {
                misPerformerName = jsonObject.getString("MIS_GROUP_NAME");
                List<MisGroup> groupList = groupDao.findByMisGroupName(misPerformerName);
                if(groupList.size() == 0){
                    return "-3";
                }
            } else if ("User".equals(misPdType)) {
                misPerformerName = jsonObject.getString("MIS_USER_NAME");
                List<MisUser> userList = userDao.findByMisUserName(misPerformerName);
                if(userList.size() == 0){
                    return "-3";
                }
            }
            if("READ".equals(misPdRight) || "WRITE".equals(misPdRight) || "DELETE".equals(misPdRight)){

            }else{
                return "-4";
            }
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) jsonArray.get(i);
            String misPdType = jsonObject.getString("MIS_PD_TYPE");
            String misPerformerName = null;
            String misPdRight = jsonObject.getString("MIS_PD_RIGHT");
            String rdRight = "";
            if ("READ".equals(misPdRight)) {
                rdRight = "3";
            } else if ("WRITE".equals(misPdRight)) {
                rdRight = "5";
            } else if ("DELETE".equals(misPdRight)) {
                rdRight = "7";
            }
            if ("Group".equals(misPdType)) {
                misPerformerName = jsonObject.getString("MIS_GROUP_NAME");
                List<MisGroup> groupList = groupDao.findByMisGroupName(misPerformerName);
                for (MisGroup group : groupList) {
                    MisPermissionDetail permissionDetail = new MisPermissionDetail();
                    permissionDetail.setMisPdType("3");
                    permissionDetail.setMisPdPerformerId(group.getMisGroupId());
                    permissionDetail.setMisPdId(SerialNumberUtils.getTableSequence("mis_permission_detail"));
                    permissionDetail.setMisPdRight(rdRight);
                    permissionDetail.setMisPermissionId(permission.getMisPermissionId());
                    permissionDetailDao.save(permissionDetail);
                }
            } else if ("User".equals(misPdType)) {
                misPerformerName = jsonObject.getString("MIS_USER_NAME");
                List<MisUser> userList = userDao.findByMisUserName(misPerformerName);
                for (MisUser user : userList) {
                    MisPermissionDetail permissionDetail = new MisPermissionDetail();
                    permissionDetail.setMisPdType("4");
                    permissionDetail.setMisPdPerformerId(user.getMisUserId());
                    permissionDetail.setMisPdId(SerialNumberUtils.getTableSequence("mis_permission_detail"));
                    permissionDetail.setMisPdRight(rdRight);
                    permissionDetail.setMisPermissionId(permission.getMisPermissionId());
                    permissionDetailDao.save(permissionDetail);
                }
            }
        }

        return "Permission Updated";
    }
    
    @Transactional
    @Override
    public String removeMember(String misPermissionId, String detail) {
        Optional<MisPermission> option = permissionDao.findById(misPermissionId);
        MisPermission permission = null;
        if(option.isPresent()){
            permission = option.get();
        }else{
            return "-5";
        }
        JSONArray jsonArray = JSONArray.parseArray(detail);
        for (int i = 0; i < jsonArray.size(); i++) {
            com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) jsonArray.get(i);
            String misPdType = jsonObject.getString("MIS_PD_TYPE");
            String misPerformerName = null;
            if ("Group".equals(misPdType)) {
                misPerformerName = jsonObject.getString("MIS_GROUP_NAME");
                List<MisGroup> groupList = groupDao.findByMisGroupName(misPerformerName);
                if(groupList.size() == 0){
                    return "-3";
                }
            } else if ("User".equals(misPdType)) {
                misPerformerName = jsonObject.getString("MIS_USER_NAME");
                List<MisUser> userList = userDao.findByMisUserName(misPerformerName);
                if(userList.size() == 0){
                    return "-3";
                }
            }

        }
        for (int i = 0; i < jsonArray.size(); i++) {
            com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) jsonArray.get(i);
            String misPdType = jsonObject.getString("MIS_PD_TYPE");
            String misPerformerName = null;
            if ("Group".equals(misPdType)) {
                misPerformerName = jsonObject.getString("MIS_GROUP_NAME");
                List<MisGroup> groupList = groupDao.findByMisGroupName(misPerformerName);
                for (MisGroup group : groupList) {
                    permissionDetailDao.deletePermissionById(permission.getMisPermissionId(),group.getMisGroupId());
                }
            } else if ("User".equals(misPdType)) {
                misPerformerName = jsonObject.getString("MIS_USER_NAME");
                List<MisUser> userList = userDao.findByMisUserName(misPerformerName);
                for (MisUser user : userList) {
                    MisPermissionDetail permissionDetail = new MisPermissionDetail();
                    permissionDetail.setMisPdPerformerId(user.getMisUserId());
                    permissionDetail.setMisPermissionId(permission.getMisPermissionId());
                    permissionDetailDao.delete(permissionDetail);
                }
            }
        }
        return "Permission Updated";
    }

}

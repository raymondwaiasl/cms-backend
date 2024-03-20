package com.asl.prd004.service.impl;

import com.asl.prd004.utils.SerialNumberUtils;
import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.dao.FolderDao;
import com.asl.prd004.dao.MemberDao;
import com.asl.prd004.dao.MisPermissionDetailDao;
import com.asl.prd004.entity.MisFolder;
import com.asl.prd004.entity.MisPermissionDetail;
import com.asl.prd004.service.IFolderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FolderServiceImpl implements IFolderService {

    @Autowired
    private FolderDao folderDao;

    @Autowired
    private MisPermissionDetailDao permissionDetailDao;

    @Autowired
    private MemberDao memberDao;

    @Override
    public List<MisFolder> getFolderList() {
        String userId = ContextHolder.getUserId();
        List<MisFolder> list = folderDao.getFolderList();

        List<MisFolder> folderList = null;
        try {
            List<MisFolder> result = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Boolean isRead = false;
                Boolean isWrite = false;
                Boolean isDelete = false;
                MisFolder misFolder =  list.get(i);
                if(null != list.get(i).getMisPermissionId() && !"".equals(list.get(i).getMisPermissionId())){
                    String permissionId=list.get(i).getMisPermissionId();
                    List<MisPermissionDetail> detailList = permissionDetailDao.getListByPermissionId(list.get(i).getMisPermissionId());
                    if(StringUtils.isEmpty(list.get(i).getMisFolderParentId())){
                        boolean isGroup = false;
                        boolean isUser = false;
                        for (int j = 0; j < detailList.size(); j++) {
                            if("3".equals(detailList.get(j).getMisPdRight()) || "5".equals(detailList.get(j).getMisPdRight()) || "7".equals(detailList.get(j).getMisPdRight())){
                                if("3".equals(detailList.get(j).getMisPdType())){
                                    String childIds = memberDao.queryChildNodeIds(detailList.get(j).getMisPdPerformerId());
                                    if(null != childIds){
                                        if(childIds.contains(userId)){
                                            isGroup = true;
                                            break;
                                        }
                                    }
                                }
                                if("4".equals(detailList.get(j).getMisPdType()) && userId.equals(detailList.get(j).getMisPdPerformerId())){
                                    isUser = true;
                                    break;
                                }
                            }
                        }
                        if(isGroup || isUser){

                        }else{
                            return result;
                        }
                    }
                    breakRead:
                    for (int j = 0; j < detailList.size(); j++) {
                        if("3".equals(detailList.get(j).getMisPdRight()) || "5".equals(detailList.get(j).getMisPdRight()) || "7".equals(detailList.get(j).getMisPdRight())){
                            if("4".equals(detailList.get(j).getMisPdType()) && userId.equals(detailList.get(j).getMisPdPerformerId())){
                                isRead = true;
                                break breakRead;
                            }
                            if("3".equals(detailList.get(j).getMisPdType())){
                                String childIds = memberDao.queryChildNodeIds(detailList.get(j).getMisPdPerformerId());
                                if(null != childIds){
                                    if(childIds.contains(userId)){
                                        isRead = true;
                                        break breakRead;
                                    }
                                }
                            }
                        }
                    }
                    misFolder.setRead(isRead);
                    breakWrite:
                    for (int j = 0; j < detailList.size(); j++) {
                        if("5".equals(detailList.get(j).getMisPdRight())||"7".equals(detailList.get(j).getMisPdRight())){
                            if("4".equals(detailList.get(j).getMisPdType()) && userId.equals(detailList.get(j).getMisPdPerformerId())){
                                isWrite = true;
                                break breakWrite;
                            }
                            if("3".equals(detailList.get(j).getMisPdType())){
                                String childIds = memberDao.queryChildNodeIds(detailList.get(j).getMisPdPerformerId());
                                if(null != childIds){
                                    if(childIds.contains(userId)){
                                        isWrite = true;
                                        break breakWrite;
                                    }
                                }
                            }
                        }
                    }
                    misFolder.setWrite(isWrite);
                    breakDelete:
                    for (int j = 0; j < detailList.size(); j++) {
                        if("7".equals(detailList.get(j).getMisPdRight())){
                            if("4".equals(detailList.get(j).getMisPdType()) && userId.equals(detailList.get(j).getMisPdPerformerId())){
                                isDelete = true;
                                break breakDelete;
                            }
                            if("3".equals(detailList.get(j).getMisPdType())){
                                String childIds = memberDao.queryChildNodeIds(detailList.get(j).getMisPdPerformerId());
                                if(null != childIds){
                                    if(childIds.contains(userId)){
                                        isDelete = true;
                                        break breakDelete;
                                    }
                                }
                            }
                        }
                    }
                    misFolder.setDelete(isDelete);
                }
                result.add(misFolder);
            }


            folderList = new ArrayList<>();
            String childs = "";
            for (int i = 0; i < result.size(); i++) {
                MisFolder folder = result.get(i);
                if(!folder.isRead() && !folder.isWrite() && !folder.isDelete()){
                    String child = folderDao.getFolderChildList(folder.getMisFolderId());
                    childs+=child;
                }
            }
            for (int i = 0; i < result.size(); i++) {
                if(!childs.contains(result.get(i).getMisFolderId())){
                    folderList.add(result.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return folderList;
    }

    @Override
    @Transactional
    public Boolean saveFolder(String parentId,String folderName) {
        MisFolder misFolder = new MisFolder();
        misFolder.setMisFolderId(SerialNumberUtils.getTableSequence("mis_folder"));
        misFolder.setMisFolderParentId(parentId);
        misFolder.setMisFolderName(folderName);
        misFolder.setDelFlag("0");
        Optional<MisFolder> folder = folderDao.findById(parentId);
        if(null != folder){
            misFolder.setMisPermissionId(folder.get().getMisPermissionId());
        }
        folderDao.saveAndFlush(misFolder);
        return true;
    }

    @Override
    @Transactional
    public MisFolder addFolder(String parentId,String folderName) {
        MisFolder misFolder = new MisFolder();
        misFolder.setMisFolderId(SerialNumberUtils.getTableSequence("mis_folder"));
        misFolder.setMisFolderParentId(parentId);
        misFolder.setMisFolderName(folderName);
        misFolder.setDelFlag("0");
        Optional<MisFolder> folder = folderDao.findById(parentId);
        if(null != folder){
            misFolder.setMisPermissionId(folder.get().getMisPermissionId());
            misFolder.setMisFolderFullPath(folder.get().getMisFolderFullPath()+"/"+folderName);
        }
        return folderDao.saveAndFlush(misFolder);
    }

    @Override
    @Transactional
    public Boolean updateFolder(String id, String name) {
        folderDao.updateFolder(id,name);
        return true;
    }

    @Override
    public Boolean deleteFolder(String misFolderId) {
        boolean flag = true;
        try{
            folderDao.deleteById(misFolderId);
        }catch (Exception e){
            flag = false;
        }
        return flag;
    }

    @Override
    @Transactional
    public Boolean updateFolderParentId(String id, String parentId) {
        folderDao.updateFolderParentId(id,parentId);
        return true;
    }


    @Override
    public String getMaxId() {
        String maxId = folderDao.getMaxId();
        return maxId;
    }
    @Override
    @Transactional
    public int updateFolderData(String folderId, String misPermissionId) {
        int fd=folderDao.updateFolderData(folderId,misPermissionId);
        return fd;
    }

    @Override
    public  MisFolder getMisFolderById(String folderId){
        Optional<MisFolder> optional = folderDao.findById(folderId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    @Override
    public List<MisFolder> findByMisFolderParentId(String misFolderParentId) {
        return folderDao.findByMisFolderParentId(misFolderParentId);
    }

    @Override
    @Transactional
    public MisFolder createFolder(String parentId, String folderName, String permissionId) {
        MisFolder misFolder = new MisFolder();
        misFolder.setMisFolderId(SerialNumberUtils.getTableSequence("mis_folder"));
        misFolder.setMisFolderParentId(parentId);
        misFolder.setMisFolderName(folderName);
        misFolder.setMisPermissionId(permissionId);
        misFolder.setDelFlag("0");
        Optional<MisFolder> folder = folderDao.findById(parentId);
        if(null != folder){
            misFolder.setMisPermissionId(folder.get().getMisPermissionId());
            misFolder.setMisFolderFullPath(folder.get().getMisFolderFullPath()+"/"+folderName);
        }
        return folderDao.saveAndFlush(misFolder);
    }

    @Override
    public Boolean isPermission(String folderId, String userId,String pdRight) {
        Optional<MisFolder> optional = folderDao.findById(folderId);
        if(optional.isPresent()){
            MisFolder folder = optional.get();
            if(StringUtils.isNotEmpty(folder.getMisPermissionId())){
                List<MisPermissionDetail> detailList = permissionDetailDao.getListByPermissionId(folder.getMisPermissionId());
                for (int j = 0; j < detailList.size(); j++) {
                    if("5".equals(pdRight)){
                        if("5".equals(detailList.get(j).getMisPdRight())||"7".equals(detailList.get(j).getMisPdRight())){
                            if("4".equals(detailList.get(j).getMisPdType()) && userId.equals(detailList.get(j).getMisPdPerformerId())){
                                return true;
                            }
                            if("3".equals(detailList.get(j).getMisPdType())){
                                String childIds = memberDao.queryChildNodeIds(detailList.get(j).getMisPdPerformerId());
                                if(null != childIds){
                                    if(childIds.contains(userId)){
                                        return true;
                                    }
                                }
                            }
                        }
                    }else if("7".equals(pdRight)){
                        if("7".equals(detailList.get(j).getMisPdRight())){
                            if("4".equals(detailList.get(j).getMisPdType()) && userId.equals(detailList.get(j).getMisPdPerformerId())){
                                return true;
                            }
                            if("3".equals(detailList.get(j).getMisPdType())){
                                String childIds = memberDao.queryChildNodeIds(detailList.get(j).getMisPdPerformerId());
                                if(null != childIds){
                                    if(childIds.contains(userId)){
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<MisFolder> findByMisFolderName(String misFolderName) {
        return folderDao.findByMisFolderName(misFolderName);
    }

    @Override
    public List<MisFolder> findByMisFolderIdAndMisFolderName(String misFolderId, String misFolderName) {
        return folderDao.findByMisFolderIdAndMisFolderName(misFolderId,misFolderName);
    }

    @Override
    public MisFolder getDefaultFolder() {
        return folderDao.getDefaultFolder();
    }
    @Override
    public String getFolderChildList(String folderId) {
        String ids = "";
        if(StringUtils.isEmpty(folderId)){
            MisFolder misFolder = getDefaultFolder();
            if(null != misFolder){
                folderId = misFolder.getMisFolderId();
                ids = folderDao.getFolderChildList(folderId);
            }
        }else{
            ids = folderDao.getFolderChildList(folderId);
        }
        return ids;
    }
}

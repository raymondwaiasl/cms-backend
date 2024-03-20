package com.asl.prd004.service.impl;

import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.WorkspaceListPageDto;
import com.asl.prd004.utils.SerialNumberUtils;
import com.asl.prd004.dao.ContextDao;
import com.asl.prd004.dao.ContextDetailDao;
import com.asl.prd004.dao.WorkspaceDao;
import com.asl.prd004.dto.ContextDTO;
import com.asl.prd004.entity.MisContext;
import com.asl.prd004.entity.MisContextDetail;
import com.asl.prd004.service.IContextService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ContextServiceImpl implements IContextService {

    @Autowired
    private ContextDao contextDao;

    @Autowired
    private ContextDetailDao contextDetailDao;

    @Autowired
    private WorkspaceDao workspaceDao;

    @Override
    public PageDataDto findContextList(PageableDto pageable) {
        Pageable page;
        if (StringUtils.isNotEmpty(pageable.getSortModel().getField())) {
            String sortField = pageable.getSortModel().getField();
            switch (sortField){
                case "misContextName":
                    sortField = "misContextName";
                    break;
                case "misRoleName":
                    sortField = "mr.misRoleName";
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
        Page<ContextDTO> contextPage = contextDao.findContextList(page);
        List<ContextDTO> contextList = contextPage.getContent();
        PageDataDto contextDto = new PageDataDto();
        contextDto.setData(contextList);
        contextDto.setTotal(contextPage.getTotalElements());
        return contextDto;
    }

    @Override
    @Transactional
    public Boolean deleteContext(String contextId) {
        try{
            contextDao.deleteById(contextId);
            contextDetailDao.deleteByContextId(contextId);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean addContext(String contextName, String roleId, String wsId) {
        String[] ws = wsId.split(",");
        MisContext misContext = new MisContext();
        misContext.setMisContextName(contextName);
        misContext.setMisContextRoleId(roleId);
        misContext.setMisContextId(SerialNumberUtils.getTableSequence("mis_context"));
        contextDao.save(misContext);
        for (int i = 0; i < ws.length; i++) {
            MisContextDetail misContextDetail = new MisContextDetail();
            misContextDetail.setMisContextId(misContext.getMisContextId());
            misContextDetail.setMisContextWsId(ws[i]);
            misContextDetail.setMisContextDetailId(SerialNumberUtils.getTableSequence("mis_context_detail"));
            contextDetailDao.save(misContextDetail);
        }
        return true;
    }
    @Override
    @Transactional
    public Boolean updateContext(String contextId,String contextName, String roleId, String wsId) {
        String[] ws = wsId.split(",");
        MisContext misContext = new MisContext();
        misContext.setMisContextId(contextId);
        misContext.setMisContextName(contextName);
        misContext.setMisContextRoleId(roleId);
        contextDao.save(misContext);
        //先删除contextDetail数据，再添加
        contextDetailDao.deleteByContextId(contextId);
        for (int i = 0; i < ws.length; i++) {
            MisContextDetail misContextDetail = new MisContextDetail();
            misContextDetail.setMisContextId(misContext.getMisContextId());
            misContextDetail.setMisContextWsId(ws[i]);
            misContextDetail.setMisContextDetailId(SerialNumberUtils.getTableSequence("mis_context_detail"));
            contextDetailDao.save(misContextDetail);
        }
        return true;
    }


    @Override
    public MisContext findById(String id) {
        return contextDao.findById(id).get();
    }

    @Override
    public List<MisContextDetail> findDetailByContextId(String contextId) {
        return contextDetailDao.findDetailByContextId(contextId);
    }

    @Override
    public List<WorkspaceListPageDto> findWorkspaceList() {
        return workspaceDao.findAllWorkspaceList();
    }

    @Override
    public WorkspaceListPageDto findWorkspaceById(String id) {
        return workspaceDao.findWorkspaceListById(id).get(0);
    }


}

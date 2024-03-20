package com.asl.prd004.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.asl.prd004.dao.*;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.*;
import com.asl.prd004.service.IWorkspaceSevice;
import com.asl.prd004.service.IOrgChartMemberService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class WorkspaceServiceImpl implements IWorkspaceSevice {

    @Autowired
    MisPropertyConfigDao propertyConfigDao;

    @Autowired
    MisPropertyConfigDetailDao propertyConfigDetailDao;

    @Autowired
    WorkspaceDao workspaceDao;

    @Autowired
    WorkSpaceWidgetDao workspaceWidgetDao;


    @Autowired
    IOrgChartMemberService orgChartService;

    @Autowired
    RoleDao roleDao;

    @Autowired
    ContextDetailDao contextDetailDao;
    @Autowired
    private WidgetDao widgetDao;

    @Override
    public PageDataDto getWorkspaceListPageable(PageableDto pageable) {
        Pageable page;
        if (StringUtils.isNotEmpty(pageable.getSortModel().getField())) {
            String sortField = pageable.getSortModel().getField();
            if (pageable.getSortModel().getSort().equalsIgnoreCase("asc")) {
                page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize(), Sort.by(sortField).ascending());
            } else {
                page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize(), Sort.by(sortField).descending());
            }
        } else {
            page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize());
        }
        Page<WorkspaceListPageDto> workspacePage = workspaceDao.findAllWorkspacePageable(page);
        List<WorkspaceListPageDto> workspaceList = workspacePage.getContent();
        PageDataDto workspaceDto = new PageDataDto();
        workspaceDto.setData(workspaceList);
        workspaceDto.setTotal(workspacePage.getTotalElements());
        return workspaceDto;
    }

    @Override
    public WorkspaceDto selectWorkspaceWidgetById(String id){
        MisWorkspace misWorkspace = workspaceDao.getMisWorkspaceByMisWorkspaceId(id);

        List<MisWorkspaceWidget> widgets = workspaceWidgetDao.getMisWorkspaceWidgetsByMisWorkspaceId(id);
        List<WorkspaceWidget> widgetList = new ArrayList<>();
        Map<String,Object> workspaceDetail = new HashMap<>();

        for(MisWorkspaceWidget w: widgets){
            WorkspaceWidget temp = toWorkspaceWidget(w);
            widgetList.add(temp);

            WidgetDto  widgetDetail = widgetDao.getMisWidgetById(temp.getMisWidgetId());
            if(widgetDetail!=null){
                workspaceDetail.put(temp.getMisWidgetId(), widgetDetail);
            }
        }

        WorkspaceDto workspaceDto = new WorkspaceDto();
        workspaceDto.setMisWorkspaceId(misWorkspace.getMisWorkspaceId());
        workspaceDto.setMisWorkspaceName(misWorkspace.getMisWorkspaceName());
        workspaceDto.setMisSortNum(misWorkspace.getMisSortNum());
        workspaceDto.setMisWorkspaceParentId(misWorkspace.getMisWorkspaceParentId());
        workspaceDto.setWidgets(widgetList);
        workspaceDto.setWidgetDetail(workspaceDetail);

        return workspaceDto;
    }

    private WorkspaceWidget toWorkspaceWidget(MisWorkspaceWidget misWorkspaceWidget){
        WorkspaceWidget widget = new WorkspaceWidget();
        WorkspaceLayout layout = new WorkspaceLayout();
        layout.setI(misWorkspaceWidget.getI());
        layout.setX(misWorkspaceWidget.getX());
        layout.setY(misWorkspaceWidget.getY());
        layout.setH(misWorkspaceWidget.getH());
        layout.setW(misWorkspaceWidget.getW());
        widget.setLayout(layout);
        widget.setMisWwId(misWorkspaceWidget.getMisWwId());
        widget.setMisWwAlias(misWorkspaceWidget.getMisWwAlias());
        widget.setMisWwTitle(misWorkspaceWidget.getMisWwTitle());
        widget.setMisBiConfigId(misWorkspaceWidget.getMisBiConfigId());
        widget.setMisWidgetId(misWorkspaceWidget.getMisWidgetId());
        widget.setMisWorkspaceId(misWorkspaceWidget.getMisWorkspaceId());
        //widget.setWidgetDetail(widgetDao.getMisWidgetById(misWorkspaceWidget.getMisWidgetId()));
        return widget;
    }

    @Override
    public boolean addWorkspace(WorkspaceDto dto) {
        MisWorkspace workspace;
        MisWorkspace mw = new MisWorkspace();
        mw.setMisWorkspaceName(dto.getMisWorkspaceName());
        mw.setMisSortNum(dto.getMisSortNum());
        mw.setMisWorkspaceId(dto.getMisWorkspaceParentId());
        workspace = workspaceDao.saveAndFlush(mw);
        List<MisWorkspaceWidget> widgetList = new ArrayList<>();
        for (WorkspaceWidget w : dto.getWidgets()) {
            MisWorkspaceWidget mww = revertWorkspaceWidget(w);
            mww.setMisWorkspaceId(workspace.getMisWorkspaceId());
            widgetList.add(mww);
        }
        workspace.setWidgets(widgetList);
        workspace = workspaceDao.saveAndFlush(workspace);
        if (null != workspace) {
            return true;
        }
        return false;
    }

    private MisWorkspaceWidget revertWorkspaceWidget(WorkspaceWidget widget){
        MisWorkspaceWidget misWorkspaceWidget = new MisWorkspaceWidget();
        misWorkspaceWidget.setI(widget.getLayout().getI());
        misWorkspaceWidget.setX(widget.getLayout().getX());
        misWorkspaceWidget.setY(widget.getLayout().getY());
        misWorkspaceWidget.setH(widget.getLayout().getH());
        misWorkspaceWidget.setW(widget.getLayout().getW());
        misWorkspaceWidget.setMisWwId(widget.getMisWwId());
        misWorkspaceWidget.setMisWwAlias(widget.getMisWwAlias());
        misWorkspaceWidget.setMisWwTitle(widget.getMisWwTitle());
        misWorkspaceWidget.setMisWidgetId(widget.getMisWidgetId());
        misWorkspaceWidget.setMisBiConfigId(widget.getMisBiConfigId());
        misWorkspaceWidget.setMisWorkspaceId(widget.getMisWorkspaceId());
        return misWorkspaceWidget;
    }

    @Override
    public boolean editWorkspace(WorkspaceDto dto) {
        MisWorkspace workspace;
        MisWorkspace mw = new MisWorkspace();
        mw.setMisWorkspaceId(dto.getMisWorkspaceId());
        mw.setMisWorkspaceName(dto.getMisWorkspaceName());
        mw.setMisSortNum(dto.getMisSortNum());
        mw.setMisWorkspaceParentId(dto.getMisWorkspaceParentId());
        List<MisWorkspaceWidget> widgetList = new ArrayList<>();
        for (WorkspaceWidget w : dto.getWidgets()) {
            MisWorkspaceWidget mww = revertWorkspaceWidget(w);
            mww.setMisWorkspaceId(mw.getMisWorkspaceId());
            widgetList.add(mww);
        }
        mw.setWidgets(widgetList);
        workspace = workspaceDao.saveAndFlush(mw);
        if (null != workspace) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteWorkspaceById(String id) {
        List<MisContextDetail> details = contextDetailDao.getMisContextDetailsByMisContextWsId(id);
        if(details.isEmpty()){
            workspaceDao.deleteByMisWorkspaceId(id);
        }else{
            throw new RuntimeException("Cannot delete workspace, it exists in context.");
        }
    }

    @Override
    public List<MenuItemDto> getContextByUserId(String userId) {
        List<String> roleIds;
        if("8888000000000001".equals(userId)){
            roleIds = roleDao.getAllRoleId();
        }else{
            roleIds = orgChartService.getRolesByUserId(userId);
        }
        Pageable pageable = PageRequest.of(0, 150, Sort.by("mw.misSortNum").ascending());
        Page<MenuItemDto> page = workspaceDao.getContextByUserIdPageable(roleIds, pageable);
        List<MenuItemDto> menuItems = page.getContent();
        for (int i = 0; i < menuItems.size(); i++) {
            String id = menuItems.get(i).getTo();
            menuItems.get(i).setTo("/dashboard/" + id);
        }
        return treeIfy(menuItems);
    }

    //把list结构转换成树结构
    private List<MenuItemDto> treeIfy(List<MenuItemDto> list){
        Map<String,MenuItemDto> id2dto = list.stream().collect(Collectors.toMap(MenuItemDto::getId,o -> o));
        List<String> ids = list.stream().map(MenuItemDto::getId).collect(Collectors.toList());
        Set<String> rootIds = list.stream().map(MenuItemDto::getId).collect(Collectors.toSet());
        //将parentId不为空的dto加入到对应的parent中
        for(String id:ids){
            MenuItemDto dto = id2dto.get(id);
            //父级id不为空时，把节点加入到父级
            if(Objects.nonNull(dto.getParent())){
                MenuItemDto parent = id2dto.get(dto.getParent());
                if(Objects.nonNull(parent)){
                    parent.getChildren().add(dto);
                    rootIds.remove(id);
                }
            }
        }
        List<MenuItemDto> rootItem = id2dto.values().stream().filter(i -> rootIds.contains(i.getId())).collect(Collectors.toList());
        //递归排序
        return sortMenuItem(rootItem);
    }

    private List<MenuItemDto> sortMenuItem(Collection<MenuItemDto> items){
        for(MenuItemDto i:items){
            if(CollectionUtil.isNotEmpty(i.getChildren())){
                i.setChildren(sortMenuItem(i.getChildren()));
            }
        }
        return CollectionUtil.sort(items,Comparator.comparingInt(MenuItemDto::getSort));
    }

    @Override
    public int validateWorkspaceById(String id) {
        List<MisContextDetail> details = contextDetailDao.getMisContextDetailsByMisContextWsId(id);
        return details.size();
    }

    @Override
    //查询可以作为parent的节点
    public List<MenuItemDto> findParentCandidateById(String id) {
        //如果子节点，不可以再成为子节点的子节点，否则会形成一个环
        //0018000000000070 test1 ----|
        //       |                   |
        //0018000000000069 test2     |
        //       |                   x 不可选
        //0018000000000074 test3 ----|
        //
        //若是查询test1节点的可选父节点，应该排除test2，test3

        //1.找出所有节点
        List<MenuItemDto> all =
            workspaceDao.findAll().stream()
                .map(w -> new MenuItemDto(w.getMisWorkspaceId(), w.getMisWorkspaceName(), w.getMisSortNum(),w.getMisWorkspaceParentId()))
                .collect(Collectors.toList());
        //2.获取节点id
        Set<String> candidateIds = all.stream().map(MenuItemDto::getId).collect(Collectors.toSet());
        //3.创建id->item的映射
        Map<String,MenuItemDto> id2item = all.stream().collect(Collectors.toMap(MenuItemDto::getId,i -> i));
        //4.树化
        treeIfy(all);
        //5.遍历子节点，从candidateIds排除掉他们
        MenuItemDto current = id2item.get(id);
        Stack<MenuItemDto> stack = new Stack<>();
        stack.push(current);
        while(!stack.isEmpty()){
            MenuItemDto pop = stack.pop();
            if(CollectionUtil.isNotEmpty(pop.getChildren())){
                pop.getChildren().forEach(c -> stack.push(c));
            }
            candidateIds.remove(pop.getId());
        }

        List<MenuItemDto> result = all.stream().filter(i -> candidateIds.contains(i.getId())).collect(Collectors.toList());
        //父级元素不为空时，增加一个空选项
        if(StrUtil.isNotBlank(current.getParent())){
            result.add(0,new MenuItemDto("-1","None",-1,null));
        }
        CollectionUtil.sort(result,Comparator.comparingInt(MenuItemDto::getSort));
        return result;
    }

    @Override
    public void setParent(MenuItemDto dto) {
        workspaceDao.setParent(dto.getId(),StrUtil.equals("-1",dto.getParent()) ? null : dto.getParent());
    }
}

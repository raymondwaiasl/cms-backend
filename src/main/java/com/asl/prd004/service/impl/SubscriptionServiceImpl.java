package com.asl.prd004.service.impl;

import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.dto.*;
import com.asl.prd004.utils.SerialNumberUtils;
import com.asl.prd004.dao.SubscriptionDao;
import com.asl.prd004.dao.SubscriptionEventDao;
import com.asl.prd004.dao.SubscriptionMsgDao;
import com.asl.prd004.entity.*;
import com.asl.prd004.service.ISubscriptionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class SubscriptionServiceImpl implements ISubscriptionService {

    @Autowired
    private SubscriptionDao subscriptionDao;

    @Autowired
    private SubscriptionEventDao subscriptionEventDao;

    @Autowired
    private SubscriptionMsgDao subscriptionMsgDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Boolean saveSubscription(String id,String typeId,Boolean checkedN,Boolean checkedM,Boolean checkedD,Boolean checkedB,String repeat,String buDate) throws Exception {
        if(checkedN){
            MisSubscription misSubscription = new MisSubscription();
            misSubscription.setMisSubscriptionId(SerialNumberUtils.getTableSequence("mis_subscription"));
            misSubscription.setMisSubscriptionType(typeId);
            misSubscription.setMisSubscriptionObjId(id);
            misSubscription.setMisSubscriptionDate(new Timestamp(System.currentTimeMillis()));
            misSubscription.setMisSubscriptionUserId(ContextHolder.getUserId());
            subscriptionDao.save(misSubscription);
            MisSubscriptionEvent subscriptionEvent = new MisSubscriptionEvent();
            subscriptionEvent.setMisSubEventId(SerialNumberUtils.getTableSequence("mis_subscription_event"));
            subscriptionEvent.setMisSubscriptionId(misSubscription.getMisSubscriptionId());
            subscriptionEvent.setMisSubEventMsg("New");
            subscriptionEvent.setMisSubEventBuRepeat("");
            subscriptionEventDao.save(subscriptionEvent);
        }

        if(checkedM){
            MisSubscription misSubscription = new MisSubscription();
            misSubscription.setMisSubscriptionId(SerialNumberUtils.getTableSequence("mis_subscription"));
            misSubscription.setMisSubscriptionType(typeId);
            misSubscription.setMisSubscriptionObjId(id);
            misSubscription.setMisSubscriptionDate(new Timestamp(System.currentTimeMillis()));
            misSubscription.setMisSubscriptionUserId(ContextHolder.getUserId());
            subscriptionDao.save(misSubscription);
            MisSubscriptionEvent subscriptionEvent = new MisSubscriptionEvent();
            subscriptionEvent.setMisSubEventId(SerialNumberUtils.getTableSequence("mis_subscription_event"));
            subscriptionEvent.setMisSubscriptionId(misSubscription.getMisSubscriptionId());
            subscriptionEvent.setMisSubEventMsg("Modified");
            subscriptionEvent.setMisSubEventBuRepeat("");
            subscriptionEventDao.save(subscriptionEvent);
        }
        if(checkedD){
            MisSubscription misSubscription = new MisSubscription();
            misSubscription.setMisSubscriptionId(SerialNumberUtils.getTableSequence("mis_subscription"));
            misSubscription.setMisSubscriptionObjId(id);
            misSubscription.setMisSubscriptionType(typeId);
            misSubscription.setMisSubscriptionDate(new Timestamp(System.currentTimeMillis()));
            misSubscription.setMisSubscriptionUserId(ContextHolder.getUserId());
            subscriptionDao.save(misSubscription);
            MisSubscriptionEvent subscriptionEvent = new MisSubscriptionEvent();
            subscriptionEvent.setMisSubEventId(SerialNumberUtils.getTableSequence("mis_subscription_event"));
            subscriptionEvent.setMisSubscriptionId(misSubscription.getMisSubscriptionId());
            subscriptionEvent.setMisSubEventMsg("Deleted");
            subscriptionEvent.setMisSubEventBuRepeat("");
            subscriptionEventDao.save(subscriptionEvent);
        }
        if(checkedB){
            MisSubscription misSubscription = new MisSubscription();
            misSubscription.setMisSubscriptionId(SerialNumberUtils.getTableSequence("mis_subscription"));
            misSubscription.setMisSubscriptionObjId(id);
            misSubscription.setMisSubscriptionType(typeId);
            misSubscription.setMisSubscriptionDate(new Timestamp(System.currentTimeMillis()));
            misSubscription.setMisSubscriptionUserId(ContextHolder.getUserId());
            subscriptionDao.save(misSubscription);
            MisSubscriptionEvent subscriptionEvent = new MisSubscriptionEvent();
            subscriptionEvent.setMisSubEventId(SerialNumberUtils.getTableSequence("mis_subscription_event"));
            subscriptionEvent.setMisSubscriptionId(misSubscription.getMisSubscriptionId());
            subscriptionEvent.setMisSubEventMsg("Bring-Up");
            buDate = buDate.substring(0,10);
            subscriptionEvent.setMisSubEventBuRepeat(repeat);
            subscriptionEvent.setMisSubEventBuDate(new SimpleDateFormat("yyyy-MM-dd").parse(buDate));
            subscriptionEventDao.save(subscriptionEvent);
        }
        return true;
    }


    @Override
    @Transactional
    public Boolean unSubscription(String objId) {
        StringBuilder querySQL = new StringBuilder("select mis_subscription_id from mis_subscription where mis_subscription_obj_id = '"+objId+"'");
        Query query = entityManager.createNativeQuery(querySQL.toString());
        List list = query.getResultList();
        for (int i = 0; i < list.size(); i++) {
            String sql = "delete from mis_subscription_event where mis_subscription_id = '"+ list.get(i)+"'";
            Query eventQuery = entityManager.createNativeQuery(sql);
            eventQuery.executeUpdate();
        }
        String querySql = "delete from mis_subscription where mis_subscription_obj_id = '"+ objId +"'";
        Query query1 = entityManager.createNativeQuery(querySql);
        query1.executeUpdate();
        return true;
    }

    @Override
    public Boolean folderIsSubscribe(String folderId) {
        StringBuilder querySQL = new StringBuilder("select 1 from mis_subscription where mis_subscription_obj_id ='"+folderId+"'");
        Query query = entityManager.createNativeQuery(querySQL.toString());
        List list = query.getResultList();
        if(list.size()>0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public PageDataDto getSubscriptionMsgListByUserId(PageableDto pageable) {
        Pageable page;
        if (StringUtils.isNotEmpty(pageable.getSortModel().getField())) {
            String sortField = pageable.getSortModel().getField();
            switch (sortField){
                case "misFolderName":
                    sortField = "mf.misFolderName";
                    break;
                case "misSubscriptionType":
                    sortField = "ms.misSubscriptionType";
                    break;
                case "misSubEventMsg":
                    sortField = "mse.misSubEventMsg";
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
        String userId = ContextHolder.getUserId();
        Page<SubscriptionMsgListDto> subscriptionPage = subscriptionMsgDao.findAllByMisSubscriptionUserId(userId, page);
        List<SubscriptionMsgListDto> subscriptionList = subscriptionPage.getContent();
        PageDataDto subscriptMsgDto = new PageDataDto();
        subscriptMsgDto.setData(subscriptionList);
        subscriptMsgDto.setTotal(subscriptionPage.getTotalElements());
        return subscriptMsgDto;
    }

    @Override
    public Boolean deleteMsg(String msgId) {
        subscriptionMsgDao.deleteById(msgId);
        return true;
    }


    @Override
    public  List<SubscriptionBuDto> getSubscriptionListByFolderId(String folderId){
        List<SubscriptionBuDto> result = subscriptionMsgDao.findAllByMisSubscriptionObjId(folderId);
        return  result;
    }

    @Override
    public  List<SubscriptionBuDto> getSubscriptionListByTypeIdAndObjId(String tableId, String ObjId){
        List<SubscriptionBuDto> result = subscriptionMsgDao.findAllByMisSubscriptionTypeAndMisSubscriptionObjId(tableId, ObjId);
        return  result;
    }

    @Override
    public  Boolean insertMsg(String subscriptionId, String subEventId){
        MisSubscriptionMsg misSubscriptionMsg = new MisSubscriptionMsg();
        misSubscriptionMsg.setMisSubscriptionMsgId(SerialNumberUtils.getTableSequence("mis_subscription"));
        misSubscriptionMsg.setMisSubscriptionId(subscriptionId);
        misSubscriptionMsg.setMisSubscriptionEventId(subEventId);
        misSubscriptionMsg.setMisSubscriptionMsgDate(new Timestamp(System.currentTimeMillis()));
        misSubscriptionMsg.setMisSubscriptionMsgHasRead("N");
        subscriptionMsgDao.save(misSubscriptionMsg);
        return  true;
    }

    @Override
    public Boolean updateMsgStatus(String msgId){
        MisSubscriptionMsg oldMsg = subscriptionMsgDao.findById(msgId).get();
        oldMsg.setMisSubscriptionMsgHasRead("Y");
        subscriptionMsgDao.save(oldMsg);
        return true;
    }

    @Override
    public List<SubscriptionBuDto> getSubscriptionListByRepeatType(String misSubEventBuRepeat) {
        List<SubscriptionBuDto> result = subscriptionMsgDao.findAllByMisSubEventBuRepeat(misSubEventBuRepeat);
        return  result;
    }


}

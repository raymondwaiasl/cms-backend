package com.asl.prd004.cron;

import cn.hutool.core.date.DateUtil;
import com.asl.prd004.dao.*;
import com.asl.prd004.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author
 * @date 2024/3/14 14:42
 */
@Component
public class FormInputEntryTask {

    @Autowired
    private FormInputRequestDao formInputRequestDao;

    @Autowired
    private FormInputRequestIndicatorDao formInputRequestIndicatorDao;

    @Autowired
    private FormInputRequestOfficeDao formInputRequestOfficeDao;

    @Autowired
    private FormInputRequestPeriodDao formInputRequestPeriodDao;

    @Autowired
    private FormInputEntryDao formInputEntryDao;

    @Autowired
    private FormInputValueDao formInputValueDao;

    @Autowired
    private MoluOfficeDao moluOfficeDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(FormInputEntryTask.class);

    private static final List<String> WORK_FLOW_TYPE_ONE = Arrays.asList("BJO", "GDETO", "WHETO");

//    @Scheduled(cron = "0 0 0 * * *") //每天凌晨执行
    @Scheduled(cron = "0 0/2 * * * *") //每2分钟执行一次(测试用)
    @Transactional
    public void executeDailyTask() {

        String formInputRequestStatus = "NS";

        List<FormInputRequestS> formInputRequestNSList = formInputRequestDao.getNSList(formInputRequestStatus);
        LOGGER.info("formInputRequestNSList===" + formInputRequestNSList);
        if (formInputRequestNSList.size() > 0) {
            for (FormInputRequestS formInputRequestS : formInputRequestNSList) {
                String id = formInputRequestS.getId();

                //get mulu office of formInputRequest
                List<FormInputRequestOfficeS> officeSList = formInputRequestOfficeDao.findByformInputRequestId(id);

                //get indicators of formInputRequest
                List<FormInputRequestindicatorS> indicatorS = formInputRequestIndicatorDao.findByformInputRequestId(id);

                //get periods of formInputRequest
                List<FormInputRequestPeriodS> periods = formInputRequestPeriodDao.findRequestPeriodByformInputRequestId(id);

                List<String> entryIds = new ArrayList<>();

                //1. generate form_input_entry record
                if (officeSList.size() > 0) {
                    for (FormInputRequestOfficeS formInputRequestOfficeS : officeSList) {
                        String moCode = moluOfficeDao.findByMoluCode(formInputRequestOfficeS.getMoluCode()).get(0).getMoCode();
                        FormInputEntryS formInputEntryS = new FormInputEntryS();
                        formInputEntryS.setFormInputRequestID(id);
                        formInputEntryS.setMoluCode(formInputRequestOfficeS.getMoluCode());
                        formInputEntryS.setMoCode(moCode);
                        formInputEntryS.setFormInputStatus("N");
                        formInputEntryS.setWorkflowType(WORK_FLOW_TYPE_ONE.contains(moCode) ? 1 : 2);
                        formInputEntryS.setDataPeriodType(periods.get(0).getDataPeriodType());
                        formInputEntryDao.save(formInputEntryS);
                        entryIds.add(formInputEntryS.getId());
                    }
                }

                //2. generate form_input_value record
                for (String entryId : entryIds) {
                    for (FormInputRequestPeriodS period : periods) {
                        Integer year = period.getYear();
                        Integer startMonth = period.getStartMonth();
                        Integer endMonth = period.getEndMonth();
                        for (FormInputRequestindicatorS indicator : indicatorS) {
                            FormInputValueS formInputValueS = new FormInputValueS();
                            formInputValueS.setFormInputEntryId(entryId);
                            formInputValueS.setYear(year);
                            formInputValueS.setStartMonth(startMonth);
                            formInputValueS.setEndMonth(endMonth);
                            formInputValueS.setIndCode(indicator.getIndCode());
                            formInputValueDao.save(formInputValueS);
                        }
                    }
                }

                //3. Change form_input_request  status=”IP”
                formInputRequestS.setFormInputRequestStatus("IP");
                formInputRequestDao.save(formInputRequestS);
            }
        }

        System.out.println("执行FormInputEntryTask任务完成: " + DateUtil.now());
    }
}

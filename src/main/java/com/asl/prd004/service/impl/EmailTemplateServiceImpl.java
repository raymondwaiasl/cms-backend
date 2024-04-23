package com.asl.prd004.service.impl;

import com.asl.prd004.dao.EmailTemplateDao;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.entity.EmailTemplateS;
import com.asl.prd004.service.IEmailTemplateService;
import com.asl.prd004.utils.SerialNumberUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Andyli
 * @date 2024/4/9 17:39
 */

@Service
public class EmailTemplateServiceImpl implements IEmailTemplateService {

    @Autowired
    private EmailTemplateDao emailTemplateDao;

    @Override
    public PageDataDto<Map<String, Object>> getList(String emailTemplateName, String emailSubject, JSONObject pageState, JSONObject sort) {

        PageDataDto<Map<String, Object>> pageDataDto = null;
        try {
            int pageNum = pageState.getInt("page") - 1;
            int pageSize = pageState.getInt("pageSize");

            Pageable pageable;

            String sortField = "id";

            if (!sort.getString("field").isEmpty()) {
                sortField = sort.getString("field");
            }

            if (sort.getString("sort").equalsIgnoreCase("asc")) {
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).ascending());
            } else {
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).descending());
            }

            Page<Map<String, Object>> emailTemplateList = emailTemplateDao.findAll(emailTemplateName, emailSubject, pageable);

            pageDataDto = new PageDataDto<>();

            pageDataDto.setData(emailTemplateList.getContent());

            pageDataDto.setTotal(emailTemplateList.getTotalElements());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pageDataDto;
    }

    @Override
    public Object getTemplateDetail(String id) {
        return emailTemplateDao.findById(id).get();
    }

    @Override
    public boolean addTemplate(String emailTemplateName, String emailSubject, String emailBody, String description) {
        try {
            EmailTemplateS emailTemplateS = new EmailTemplateS();
            emailTemplateS.setId(SerialNumberUtils.getTableSequence("email_template_s"));
            emailTemplateS.setEmailTemplateName(emailTemplateName);
            emailTemplateS.setEmailSubject(emailSubject);
            emailTemplateS.setEmailBody(emailBody);
            emailTemplateS.setDescription(description);
            emailTemplateDao.saveAndFlush(emailTemplateS);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean editTemplate(String id, String emailTemplateName, String emailSubject, String emailBody, String description) {
        EmailTemplateS emailTemplateS = emailTemplateDao.findById(id).get();
        if (emailTemplateS != null) {
            emailTemplateS.setEmailTemplateName(emailTemplateName);
            emailTemplateS.setEmailSubject(emailSubject);
            emailTemplateS.setEmailBody(emailBody);
            emailTemplateS.setDescription(description);
            emailTemplateDao.saveAndFlush(emailTemplateS);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteTemplate(String id) {
        try {
            emailTemplateDao.deleteById(id);
            return true;
        } catch (Exception e) {
            //throw new RuntimeException(e);
            return false;
        }
    }
}

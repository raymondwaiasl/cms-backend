package com.asl.prd004.service;

import com.asl.prd004.dto.PageDataDto;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author Andyli
 * @date 2024/4/9 17:30
 */
public interface IEmailTemplateService {

    PageDataDto<Map<String, Object>> getList(String emailTemplateName, String emailSubject, JSONObject pageState, JSONObject sort);

    Object getTemplateDetail(String id);

    boolean addTemplate(String emailTemplateName, String emailSubject, String emailBody, String description);

    boolean editTemplate(String id, String emailTemplateName, String emailSubject, String emailBody, String description);

    boolean deleteTemplate(String id);
}

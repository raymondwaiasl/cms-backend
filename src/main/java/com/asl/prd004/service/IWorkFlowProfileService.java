package com.asl.prd004.service;

import com.asl.prd004.dto.PageDataDto;
import org.json.JSONObject;

/**
 * @description: TODO
 * @author: billy
 * @date: 2023/10/24 18:07
 * @version: 1.0
 */
public interface IWorkFlowProfileService {
    PageDataDto getAllWorkProfile(JSONObject pageState, JSONObject sort);

    int addNewTypeByFlow(JSONObject json);
}

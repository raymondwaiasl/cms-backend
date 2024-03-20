package com.asl.prd004.service;

import com.asl.prd004.entity.MisWelcome;

import java.util.List;

/**
 * @description: TODO
 * @author: billy
 * @date: 2023/10/10 16:07
 * @version: 1.0
 */
public interface IWelcomeService {
    boolean addWelcome(String content);

    String getWelcome();
}

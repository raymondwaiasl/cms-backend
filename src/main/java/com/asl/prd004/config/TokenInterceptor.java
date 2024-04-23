package com.asl.prd004.config;

import com.asl.prd004.utils.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@CrossOrigin
public class TokenInterceptor implements HandlerInterceptor {
    /**
     * 请求头
     */
    private static final String HEADER_AUTH = "authorization";

    /**
     * 安全的url，不需要令牌
     */
    private static final List<String> SAFE_URL_LIST = Arrays.asList("/userInfo/login", "userInfo/register", "/userInfo/forgotPwd", "/userInfo/reset","/userInfo/getPasswordPolicy" ,"/files/**");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        response.setContentType("application/json; charset=utf-8");

        String url = request.getRequestURI().substring(request.getContextPath().length());
        System.out.println(url);
        // 登录和注册等请求不需要令牌
        AntPathMatcher matcher = new AntPathMatcher();
        for(String safeUrl:SAFE_URL_LIST){
            if(matcher.match(safeUrl,url)){
                return true;
            }
        }

        // 从请求头里面读取token
        String token = request.getHeader(HEADER_AUTH);
        if (token == null) {
            throw new DefinitionException(403, "Token is empty!");
        }

        // 解析令牌
        Map<String, Object> map = JwtUtil.resolveToken(token);
        String userId = map.get("userId").toString();
        String userLoginId = map.get("userLoginId").toString();
        String office = map.get("office").toString();
        String userRole = map.get("userRole").toString();
        ContextHolder.setUserId(userId);
        ContextHolder.setUserLoginId(userLoginId);
        ContextHolder.setOffice(office);
        ContextHolder.setUserRole(userRole);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ContextHolder.shutdown();
    }
}


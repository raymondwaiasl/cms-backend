package com.asl.prd004.utils;

import cn.hutool.core.date.DateUtil;
import com.asl.prd004.config.DefinitionException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author llh
 */
public class JwtUtil {
    /**
     * 令牌密码 不少于32位
     */
    private static final String SECRET = "asl-2022";

    /**
     * 令牌前缀
     */
    private static final String TOKEN_PREFIX = "Bearer";

    /**
     * 令牌过期时间
     */
//    public static final Integer EXPIRE_SECONDS = 60 * 60 * 24 * 1;
    public static final long EXPIRE_SECONDS =  60 * 60 * 1000;
    /**
     * 生成令牌
     */
    public static String generateToken(Map<String, Object> map) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRE_SECONDS);
        String jwt = Jwts.builder()
                .setSubject("user info").setClaims(map)
                .signWith(SignatureAlgorithm.HS512, SECRET)
//                .setExpiration(DateUtil.offsetSecond(new Date(), EXPIRE_SECONDS))
                .setExpiration(expiration)
                .compact();
        return TOKEN_PREFIX + "_" + jwt;
    }

    /**
     * 验证令牌
     */
    public static Map<String, Object> resolveToken(String token) {
        if (token == null) {
            throw new DefinitionException(403, "Token is empty!");
        }
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replaceFirst(TOKEN_PREFIX + "_", ""))
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new DefinitionException(403, "Token has expired!");
        } catch (Exception e) {
            throw new DefinitionException(403, "Token parsing exception.");
        }
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> map1 = new HashMap<>();
        map.put("email","807661792@163.com");
        String t = generateToken(map);
        map1 = resolveToken(t);
        System.out.println(map1.get("email"));
    }
}
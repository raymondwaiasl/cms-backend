package com.asl.prd004.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StrUtil extends cn.hutool.core.util.StrUtil{
    public static String nullToEmpty(Object obj){
        return Objects.nonNull(obj) ? String.valueOf(obj) : "";
    }

    public static String format(String template, String prefix, String suffix, Map<?, ?> map){
        if (null == template) {
            return null;
        } else if (null != map && !map.isEmpty()) {
            String template2 = template.toString();
            Iterator var5 = map.entrySet().iterator();

            while(true) {
                String value;
                Map.Entry entry;
                do {
                    if (!var5.hasNext()) {
                        return template2;
                    }

                    entry = (Map.Entry)var5.next();
                    value = utf8Str(entry.getValue());
                } while(null == value);

                template2 = replaceIgnoreCase(template2, prefix + entry.getKey() + suffix, value);
            }
        } else {
            return template.toString();
        }
    }

    public static void main(String[] args) {
        Long[] dd = new Long[]{0l,1l,2l};
        List<Long> map = ArrayUtil.map(dd, e -> Long.valueOf(e.toString()));
    }
}

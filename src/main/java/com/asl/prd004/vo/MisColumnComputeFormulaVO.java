package com.asl.prd004.vo;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.asl.prd004.config.DefinitionException;
import com.asl.prd004.enums.ResultCodeEnum;
import com.asl.prd004.enums.TableMgmtColumnType;
import com.asl.prd004.utils.StrUtil;
import lombok.Data;
import java.sql.Timestamp;
import java.util.Date;
@Data
public class MisColumnComputeFormulaVO {
    private String misColumnId1;
    private String operator;
    private String misColumnId2;

    public static Object calc(TableMgmtColumnType columnType, Object val1, String op, Object val2){
        //字符串
        if(TableMgmtColumnType.String == columnType){
            if("+".equals(op)){
                return StrUtil.nullToEmpty(val1) + StrUtil.nullToEmpty(val2);
            }
        }
        //日期
        if(TableMgmtColumnType.Date == columnType){
            if(val1 instanceof Timestamp){
                val1 = new Date(((Timestamp)val1).getTime());
            }
            if(val1 instanceof String){
                val1 = DateUtil.parse((String) val1);
            }
            if(val2 instanceof Timestamp){
                val2 = new Date(((Timestamp)val2).getTime());
            }
            if("+".equals(op)){
                return DateUtil.offsetDay(
                        (Date)val1,
                        val2 instanceof Integer ?
                                (Integer) val2 :
                                Integer.parseInt((String) val2));
            }
            if("-".equals(op)){
                if(val2 instanceof String){
                    val2 = DateUtil.parse((String) val2);
                }
                return DateUtil.between((Date)val1,(Date)val2, DateUnit.DAY,true);
            }
        }
        //数值
        if(TableMgmtColumnType.Integer == columnType || TableMgmtColumnType.Double == columnType){
            if(val1 instanceof String){
                val1 = NumberUtil.parseNumber((String)val1);
            }
            if(val2 instanceof String){
                val2 = NumberUtil.parseNumber((String)val2);
            }
            if("+".equals(op)){
                return NumberUtil.add((Number)val1,(Number)val2);
            }
            if("-".equals(op)){
                return NumberUtil.sub((Number)val1,(Number)val2);
            }
            if("*".equals(op)){
                return NumberUtil.mul((Number)val1,(Number)val2);
            }
            if("/".equals(op)){
                return NumberUtil.div((Number)val1,(Number)val2);
            }
        }
        throw new DefinitionException(ResultCodeEnum.FAIL.getCode(),
                String.format("Calculation failed,columnType:%s,val1:%s,op:%s,val2:%s",columnType,val1,op,val2));
    }
//
//    public static void main(String[] args) {
//        DateTime parse = DateUtil.parse("2023-08-14 08:30:00", DatePattern.NORM_DATETIME_PATTERN);
////        DateTime parse2 = DateUtil.parse("2023-08-15 07:30:00", DatePattern.NORM_DATETIME_PATTERN);
////        System.out.println(DateUtil.between(parse,parse2, DateUnit.DAY,true));
//        DateTime dateTime = DateUtil.offsetDay(parse, -2);
//        System.out.println(dateTime);
//        DateTime parse = DateUtil.parse("2023-10-27 12:34:56");
//        DateTime parse1 = DateUtil.parse("2023-11-27");
//
//        System.out.println("'" + MisColumnComputeFormulaVO.calc(TableMgmtColumnType.Date,parse1,"+","4") + "'");
//        System.out.println(parse1);
//    }
}

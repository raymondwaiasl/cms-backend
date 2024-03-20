package com.asl.prd004.service.impl;

import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.dao.MisAuditDetailDao;
import com.asl.prd004.dao.MisAuditLogDao;
import com.asl.prd004.dao.MisUserDao;
import com.asl.prd004.entity.MisAuditDetail;
import com.asl.prd004.entity.MisAuditLog;
import com.asl.prd004.service.IAuditLogService;
import com.asl.prd004.utils.Log;
import com.asl.prd004.vo.AuditLogVO;
import com.asl.prd004.vo.LogVO;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Aspect
@Component
public class LogAspect {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private MisAuditLogDao misAuditLogDao;
    @Autowired
    private MisAuditDetailDao auditDetailDao;
    @Autowired
    private MisUserDao misUserDao;
    @Autowired
    private IAuditLogService auditLogService;
    @Autowired
    private EntityManager entityManager;

    @Pointcut("@annotation(com.asl.prd004.utils.Log)")
    public void pointcut() { }
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        long beginTime = System.currentTimeMillis();
        try {
            // 执行方法
            result = point.proceed();
            System.out.println("c" + result);
            // 执行时长(毫秒)
            long time = System.currentTimeMillis() - beginTime;
            // 保存日志
            saveLog(point, time, result);
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
            return result;
        }
    }
    private void saveLog(ProceedingJoinPoint joinPoint, long time, Object result) throws Exception{
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            // 请求的方法名
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = signature.getName();
            String auditOperation = "";
            MisAuditLog sysLog = new MisAuditLog();
            Log logAnnotation = method.getAnnotation(Log.class);
            if (logAnnotation != null) {
                // 注解上的描述
                auditOperation = logAnnotation.value();
            }

            sysLog.setMisAuditOperation(auditOperation);
            sysLog.setMisAuditMethod(className + "." + methodName + "()");
            // 请求的方法参数值
            Object[] args = joinPoint.getArgs();
            // 请求的方法参数名称
            LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
            String[] paramNames = u.getParameterNames(method);
            if (args != null && paramNames != null) {
                String params = "";
                for (int i = 0; i < args.length; i++) {
                    params += "  " + paramNames[i] + ": " + args[i];
                }
                sysLog.setMisAuditParams(params);
            }
            // 获取request
            //HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
            String ipAddress=String.valueOf(getLocalIp4Address());
            // 设置IP地址
            sysLog.setMisAuditIp(ipAddress!=null?ipAddress.replaceAll("Optional",""):"");
            String userName = misUserDao.getUserInfoByUserId(ContextHolder.getUserId()) != null ? misUserDao.getUserInfoByUserId(ContextHolder.getUserId()).get(0).getMisUserName() : "";
            // 模拟一个用户名
            sysLog.setMisAuditUser(userName);
            sysLog.setMisAuditTime((int) time);
            Date da=new Date();//取当前时间
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");//转换时间格式
            String formatDa = sf.format(da);
            sysLog.setCreateTime(formatDa);
            //sysLog.setMisAuditId(SerialNumberUtils.getTableSequence(  "mis_audit_log"));
            // 保存系统日志
            sysLog=misAuditLogDao.saveAndFlush(sysLog);
            if(null != sysLog){
                if("Insert table data.".equals(auditOperation)){
                    System.out.println("result========" + ((AuditLogVO) result).getRecordId());
                    String typeId = ((AuditLogVO) result).getTypeId();
                    String tableName = ((AuditLogVO) result).getTableName();
                    String recordId = ((AuditLogVO) result).getRecordId();
                    if(!StringUtils.isEmpty(tableName) && !StringUtils.isEmpty(recordId) && !StringUtils.isEmpty(typeId)){
                        MisAuditDetail auditDetail = new MisAuditDetail();
                        auditDetail.setMisAuditId(sysLog.getMisAuditId());
                        auditDetail.setMisAuditTypeId(typeId);
                        auditDetail.setMisAuditRecId(recordId.substring(1,17));
                        auditDetail.setMisOperator(ContextHolder.getUserId());
                        auditDetail.setMisOperationTime(new Timestamp(System.currentTimeMillis()));
                        String querySql = "select a.`action` ,CONVERT(a.uid,char) from " + tableName + "_log a WHERE a.id = " + recordId + " order by a.transaction_date desc";
                        List<Object[]> resultList = entityManager.createNativeQuery(querySql).getResultList();
                        List<LogVO> voList = fillDataList(resultList,LogVO.class,null);
                        if(voList.size() == 1){
                            auditDetail.setMisAuditDtlAction(voList.get(0).getAction());
                            auditDetail.setMisAuditRechistBfid("");
                            auditDetail.setMisAuditRechistAfid(voList.get(0).getUid());
                            auditDetailDao.saveAndFlush(auditDetail);
                        }else if(voList.size() == 2){
                            auditDetail.setMisAuditDtlAction(voList.get(0).getAction());
                            auditDetail.setMisAuditRechistAfid(voList.get(0).getUid());
                            auditDetail.setMisAuditRechistBfid(voList.get(1).getUid());
                            auditDetailDao.saveAndFlush(auditDetail);
                        }
                        System.out.println("result is =====" + voList.get(0).getAction());
                    }

                }
            }

        } catch (Exception e) {
            System.out.println("error is :"+e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
    //获取客户端IP地址
    private String getIpAddress() {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknow".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length () == 0 || "unknown".equalsIgnoreCase (ip)) {
            ip = request.getHeader ("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length () == 0 || "unknown".equalsIgnoreCase (ip)) {
            ip = request.getRemoteAddr ();
            if (ip.equals ("127.0.0.1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost ();
                } catch (Exception e) {
                    e.printStackTrace ();
                }
                ip = inet.getHostAddress ();
            }
        }
        // 多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length () > 15) {
            if (ip.indexOf (",") > 0) {
                ip = ip.substring (0, ip.indexOf (","));
            }
        }
        System.out.println("ip===="+ip);
        return ip;

    }
    public static Optional<Inet4Address> getLocalIp4Address() throws SocketException {
        final List<Inet4Address> ipByNi = getLocalIp4AddressFromNetworkInterface();
        if (ipByNi.isEmpty() || ipByNi.size() > 1) {
            final Optional<Inet4Address> ipBySocketOpt = getIpBySocket();
            if (ipBySocketOpt.isPresent()) {
                return ipBySocketOpt;
            } else {
                return ipByNi.isEmpty() ? Optional.empty() : Optional.of(ipByNi.get(0));
            }
        }
        return Optional.of(ipByNi.get(0));
    }

    public static List<Inet4Address> getLocalIp4AddressFromNetworkInterface() throws SocketException {
        List<Inet4Address> addresses = new ArrayList<>(1);
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        if (e == null) {
            return addresses;
        }
        while (e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            if (!isValidInterface(n)) {
                continue;
            }
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {
                InetAddress i = (InetAddress) ee.nextElement();
                if (isValidAddress(i)) {
                    addresses.add((Inet4Address) i);
                }
            }
        }
        return addresses;
    }

    /**
     * 过滤回环网卡、点对点网卡、非活动网卡、虚拟网卡并要求网卡名字是eth或ens开头
     *
     * @param ni 网卡
     * @return 如果满足要求则true，否则false
     */
    private static boolean isValidInterface(NetworkInterface ni) throws SocketException {
        return !ni.isLoopback() && !ni.isPointToPoint() && ni.isUp() && !ni.isVirtual()
                && (ni.getName().startsWith("eth") || ni.getName().startsWith("ens"));
    }

    /**
     * 判断是否是IPv4，并且内网地址并过滤回环地址.
     */
    private static boolean isValidAddress(InetAddress address) {
        return address instanceof Inet4Address && address.isSiteLocalAddress() && !address.isLoopbackAddress();
    }


    private static Optional<Inet4Address> getIpBySocket() throws SocketException {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            if (socket.getLocalAddress() instanceof Inet4Address) {
                return Optional.of((Inet4Address) socket.getLocalAddress());
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    /**
     * 支持内部类的转换
     * Object[] 内元素顺序需与目标类属性顺序一致
     */
    private <T, O> List<T> fillDataList(List<Object[]> list, Class<T> t, Class<O> outer) {
        ArrayList<T> ts = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return ts;
        }
        for (Object[] o : list) {
            T t1 = fillData(o, t, outer);
            ts.add(t1);
        }
        return ts;
    }

    @SneakyThrows
    private <T, O> T fillData(Object[] o, Class<T> t, Class<O> outerT) {
        T t1;
        int size;
        Field[] fields = t.getDeclaredFields();
        if (null == outerT) {
            size = Math.min(o.length, fields.length);
            t1 = t.newInstance();
        } else {
            // 内部类有一个隐藏属性：外部类的引用
            size = Math.min(o.length, fields.length - 1);

            Class<?> aClass = Class.forName(t.getName());
            Class<?> outerClass = Class.forName(outerT.getName());
            t1 = (T) aClass.getDeclaredConstructor(outerClass).newInstance(outerClass.newInstance());
        }
        for (int i = 0; i < size; i++) {
            Object o1 = o[i];
            Field field = fields[i];
            field.setAccessible(true);
            String typeName = field.getGenericType().getTypeName();
            // 支持String，Integer，Long，BigDecimal类型
            if (String.class.getName().equals(typeName)) {
                o1 = null == o1 ? null : o1.toString();
            }
            if (Integer.class.getName().equals(typeName)) {
                o1 = null == o1 ? null : Integer.valueOf(o1.toString());
            }
            if (Long.class.getName().equals(typeName)) {
                o1 = null == o1 ? null : Long.valueOf(o1.toString());
            }
            if (BigDecimal.class.getName().equals(typeName)) {
                o1 = null == o1 ? null : new BigDecimal(o1.toString());
            }
            field.set(t1, o1);
        }
        return t1;
    }

}

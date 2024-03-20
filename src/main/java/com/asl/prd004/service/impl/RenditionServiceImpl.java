package com.asl.prd004.service.impl;

import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.dao.CmsFormatDao;
import com.asl.prd004.dao.CmsRenditionDao;
import com.asl.prd004.dao.MisCmsVersionDao;
import com.asl.prd004.dao.MisUserDao;
import com.asl.prd004.dto.RenditionDto;
import com.asl.prd004.entity.CmsFormat;
import com.asl.prd004.entity.CmsRendition;
import com.asl.prd004.entity.MisUser;
import com.asl.prd004.service.IRenditionService;
import com.asl.prd004.utils.Base64Util;
import com.asl.prd004.utils.JSchUtil;
import com.asl.prd004.utils.SerialNumberUtils;
import com.asl.prd004.utils.SmbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.asl.prd004.utils.ConstUtils.LINUXFILEPATH;

@Transactional
@Service
public class RenditionServiceImpl implements IRenditionService {

    @Autowired
    CmsRenditionDao renditionDao;

    @Autowired
    CmsFormatDao formatDao;

    @Autowired
    MisUserDao userDao;

    @Autowired
    private MisCmsVersionDao misCmsVersionDao;


    @Override
    public List<RenditionDto> getRenditionByRecordId(String recordId) {
        List<CmsRendition> renditions = renditionDao.getCmsRenditionsByMisRecordId(recordId);
        List<RenditionDto> renditionDtoList = new ArrayList<>();
        for(CmsRendition r:renditions){
            RenditionDto t = new RenditionDto();
            Optional<CmsFormat> format = formatDao.findById(r.getCmsFormatId());
            List<MisUser> users = userDao.getUserInfoByUserId(r.getCmsCreatorUserId());
            if(format.isPresent()){
                t.setFormat(format.get());
            }
            if(users.size()>0){
                t.setUser(users.get(0));
            }
            t.setCmsRenditionDate(r.getCmsRenditionDate());
            t.setCmsRenditionId(r.getCmsRenditionId());
            t.setCmsIsPrimary(r.getCmsIsPrimary());
            t.setMisRecordId(r.getMisRecordId());
            t.setMisTypeId(r.getMisTypeId());
            renditionDtoList.add(t);
        }
        return renditionDtoList;
    }

    @Override
    public boolean upLoadRendition(MultipartFile[] files, String misTypeId, String misRecordId){
        if (null == files) {
            return false;
        }
        if (files.length > 0) {
            int i = 0;
            for (MultipartFile mul : files) {
                String fileName = getRenditionName(mul.getOriginalFilename());
                System.out.println(mul.getName() + "===" + fileName);
                String liunxDir =LINUXFILEPATH;
                CmsRendition rendition = new CmsRendition();
                rendition.setCmsFileLocation("\\\\" + liunxDir + "/" + fileName);
                rendition.setCmsRenditionFile(fileName);
                rendition.setCmsCreatorUserId(ContextHolder.getUserId());
                rendition.setCmsFormatId(getFormatId(fileName));
                rendition.setCmsIsPrimary("N");
                rendition.setCmsRenditionDate(new Timestamp(System.currentTimeMillis()));
                rendition.setMisRecordId(misRecordId);
                rendition.setMisTypeId(misTypeId);
                rendition = renditionDao.save(rendition);
                try {
                    new JSchUtil().uploadFile(liunxDir, mul.getInputStream(), mul.getOriginalFilename());
                } catch (IOException e) {
                    //throw new RuntimeException(e);
                    return false;
                }
                /*if(null != rendition.getCmsRenditionId()){
                    SmbUtil.smbPut(liunxDir,mul,fileName);
                }*/
            }

        }
        return true;
    }

    @Override
    public boolean deleteRenditionByRenditionId(String renditionId) {
        CmsRendition rendition = renditionDao.getCmsRenditionByCmsRenditionId(renditionId);
        if(null != rendition){
            if(!"Y".equals(rendition.getCmsIsPrimary())){
                String shareDir = "192.168.50.174/test";
                SmbUtil.deleteFile(shareDir,rendition.getCmsRenditionFile());
                renditionDao.delete(rendition);
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    private String getFormatId(String fileName){
        String[] strArr = fileName.split("\\.");
        List<CmsFormat> formats = formatDao.getCmsFormatsByCmsFormatDosExt(strArr[strArr.length-1]);
        return formats.get(0).getCmsFormatId();

    }

    private String getRenditionName(String fileName){
        String currentDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String[] strArr = fileName.split("\\.");
        return fileName.substring(0,fileName.length()-strArr[strArr.length-1].length()-1) + "_" + currentDate + "." + strArr[strArr.length-1];
    }
    @Override
    public boolean saveRendition(String recordId,String typeId,String image,String format) {
        String liunxDir = LINUXFILEPATH;
        String path = System.getProperty("user.dir")+ File.separator;
        String cmsVersionId= SerialNumberUtils.getTableSequence("mis_cms_version");
        String cmsRenditionId=SerialNumberUtils.getTableSequence("cms_rendition");
        String fileName = typeId+"-"+cmsRenditionId+"-"+cmsVersionId+"."+format;
        Base64Util.generateImage(image,path+fileName);
        CmsRendition rendition = new CmsRendition();
        rendition.setCmsRenditionId(cmsRenditionId);
        rendition.setCmsFileLocation(liunxDir + "/" + fileName);
        rendition.setCmsRenditionFile(fileName);
        rendition.setCmsCreatorUserId(ContextHolder.getUserId());
        if("pdf".equals(fileName.split("\\.")[1])){
            rendition.setCmsFormatId("0100000000000241");
        }else{
            rendition.setCmsFormatId(getFormatId(fileName));
        }
        rendition.setCmsIsPrimary("Y");
        rendition.setCmsRenditionDate(new Timestamp(System.currentTimeMillis()));
        rendition.setMisRecordId(recordId);
        rendition.setMisTypeId(typeId);
        rendition.setCmsCreatorUserId(ContextHolder.getUserId());

        try {
            renditionDao.saveAndFlush(rendition);
            FileInputStream inputStream = new FileInputStream(new File(fileName));
            new JSchUtil().uploadFile(liunxDir, inputStream,fileName);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new File(fileName).delete();
        return true;
    }

    @Override
    public List<CmsRendition> getCmsRenditionsByMisRecordId(String recordId) {
        return renditionDao.getCmsRenditionsByMisRecordId(recordId);
    }
}

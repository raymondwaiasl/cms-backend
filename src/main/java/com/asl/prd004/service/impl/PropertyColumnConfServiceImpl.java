package com.asl.prd004.service.impl;

import com.asl.prd004.dao.MisPropertyColumnConfCondDao;
import com.asl.prd004.dao.MisPropertyColumnConfDao;
import com.asl.prd004.dto.PropertyColumnCondDto;
import com.asl.prd004.entity.MisPropertyColumnConf;
import com.asl.prd004.entity.MisPropertyColumnConfCond;
import com.asl.prd004.entity.MisPropertyConfig;
import com.asl.prd004.entity.MisPropertyConfigDetail;
import com.asl.prd004.service.IPropertyColumnConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

import static com.asl.prd004.utils.CastEntity.castEntity;

@Service
@Transactional
public class PropertyColumnConfServiceImpl implements IPropertyColumnConfService {
    @Autowired
    MisPropertyColumnConfDao misPropertyColumnConfDao;

    @Autowired
    MisPropertyColumnConfCondDao misPropertyColumnConfCondDao;


    public  PropertyColumnCondDto getPropertyColumnConfByColumnConfigId(String columnConfigId){
        PropertyColumnCondDto propertyColumnCondDto = new PropertyColumnCondDto();
        Optional<MisPropertyColumnConf> optional = misPropertyColumnConfDao.findById(columnConfigId);
        if (optional.isPresent()){
            MisPropertyColumnConf misPropertyColumnConf = optional.get();
            if (misPropertyColumnConf != null){
                propertyColumnCondDto.setMisPropertyColConfId(misPropertyColumnConf.getMisPropertyColConfId());
                propertyColumnCondDto.setMisPropertyConfigDetailId(misPropertyColumnConf.getMisPropertyConfigDetailId());
                propertyColumnCondDto.setMisPropertyConfigRegularExpression(misPropertyColumnConf.getMisPropertyConfigRegularExpression());

                // convert array list to object
                List<MisPropertyColumnConfCond>  columnConfConds =
                        (List<MisPropertyColumnConfCond>) misPropertyColumnConf.getColumnConfConds();
                ArrayList<MisPropertyColumnConfCond>  typeOneConds = new ArrayList<>();
                ArrayList<MisPropertyColumnConfCond>  typeTwoConds = new ArrayList<>();
                ArrayList<MisPropertyColumnConfCond>  typeThreeConds = new ArrayList<>();
                ArrayList<MisPropertyColumnConfCond>  typeFourConds = new ArrayList<>();

                for(MisPropertyColumnConfCond columnConfCond : columnConfConds){
                    if(columnConfCond.getMisPropertyColConfType().equals("1")){
                        typeOneConds.add(columnConfCond);
                    }
                    if(columnConfCond.getMisPropertyColConfType().equals("2")){
                        typeTwoConds.add(columnConfCond);
                    }
                    if(columnConfCond.getMisPropertyColConfType().equals("3")){
                        typeThreeConds.add(columnConfCond);
                    }
                    if(columnConfCond.getMisPropertyColConfType().equals("4")){
                        typeFourConds.add(columnConfCond);
                    }
                }
                LinkedHashMap<String, ArrayList<MisPropertyColumnConfCond>> propertyConfigDetail =
                        new LinkedHashMap<>();
                propertyConfigDetail.put("1", typeOneConds);
                propertyConfigDetail.put("2", typeTwoConds);
                propertyConfigDetail.put("3", typeThreeConds);
                propertyConfigDetail.put("4", typeFourConds);
                propertyColumnCondDto.setColumnConfigConditions(propertyConfigDetail);

                return propertyColumnCondDto;
            }
        }
        return  null;
    }

   public   Boolean addPropertyColumnConfByColumnConfigId(PropertyColumnCondDto propertyColumnCondDto) throws IllegalAccessException {
       MisPropertyColumnConf propertyColumnConf;
       MisPropertyColumnConf mpc = new MisPropertyColumnConf();
       mpc.setMisPropertyColConfId(propertyColumnCondDto.getMisPropertyColConfId());
       mpc.setMisPropertyConfigDetailId(propertyColumnCondDto.getMisPropertyConfigDetailId());
       mpc.setMisPropertyConfigRegularExpression(propertyColumnCondDto.getMisPropertyConfigRegularExpression());
       propertyColumnConf = misPropertyColumnConfDao.saveAndFlush(mpc);
       List<MisPropertyColumnConfCond> columnConfCondDaos = new ArrayList<>();

       // Convert object to array list
       LinkedHashMap<String, ArrayList<MisPropertyColumnConfCond>> propertyConfigDetail = propertyColumnCondDto.getColumnConfigConditions();
       Iterator iter = propertyConfigDetail.entrySet().iterator();
       while ( iter.hasNext()) {
           Map.Entry entry = (Map.Entry) iter.next();
           ArrayList<MisPropertyColumnConfCond>  columnConfConds = (ArrayList<MisPropertyColumnConfCond>) entry.getValue();
           for( MisPropertyColumnConfCond columnConfCond : columnConfConds){
               columnConfCond.setMisPropertyColConfId(propertyColumnConf.getMisPropertyColConfId());
               columnConfCondDaos.add(columnConfCond);
           }
       }
       propertyColumnConf.setColumnConfConds(columnConfCondDaos);
       propertyColumnConf = misPropertyColumnConfDao.saveAndFlush(propertyColumnConf);
       if (null != propertyColumnConf) {
           return true;
       }
       return false;
   }

    public   Boolean editPropertyColumnConfByColumnConfigId(PropertyColumnCondDto propertyColumnCondDto) throws IllegalAccessException {
        MisPropertyColumnConf propertyColumnConf;
        MisPropertyColumnConf mpc = new MisPropertyColumnConf();
        mpc.setMisPropertyColConfId(propertyColumnCondDto.getMisPropertyColConfId());
        mpc.setMisPropertyConfigDetailId(propertyColumnCondDto.getMisPropertyConfigDetailId());
        mpc.setMisPropertyConfigRegularExpression(propertyColumnCondDto.getMisPropertyConfigRegularExpression());
        List<MisPropertyColumnConfCond> columnConfCondDaos = new ArrayList<>();

        // Convert object to array list
        LinkedHashMap<String, ArrayList<MisPropertyColumnConfCond>> propertyConfigDetail = propertyColumnCondDto.getColumnConfigConditions();
        Iterator iter = propertyConfigDetail.entrySet().iterator();
        while ( iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            ArrayList<MisPropertyColumnConfCond>  columnConfConds = (ArrayList<MisPropertyColumnConfCond>) entry.getValue();
            for( MisPropertyColumnConfCond columnConfCond : columnConfConds){
                columnConfCond.setMisPropertyColConfId(propertyColumnCondDto.getMisPropertyColConfId());
                columnConfCondDaos.add(columnConfCond);
            }
        }

        mpc.setColumnConfConds(columnConfCondDaos);
        propertyColumnConf = misPropertyColumnConfDao.saveAndFlush(mpc);
        if (null != propertyColumnConf) {
            return true;
        }
        return false;
    }

    public  PropertyColumnCondDto getPropertyColumnConfByMisPropertyConfigDetailId(String columnConfigId){
        PropertyColumnCondDto propertyColumnCondDto = new PropertyColumnCondDto();
        Optional<MisPropertyColumnConf> optional = misPropertyColumnConfDao.findByMisPropertyConfigDetailId(columnConfigId);

        if (optional.isPresent()){
            MisPropertyColumnConf misPropertyColumnConf = optional.get();
            if (misPropertyColumnConf != null){
                propertyColumnCondDto.setMisPropertyColConfId(misPropertyColumnConf.getMisPropertyColConfId());
                propertyColumnCondDto.setMisPropertyConfigDetailId(misPropertyColumnConf.getMisPropertyConfigDetailId());
                propertyColumnCondDto.setMisPropertyConfigRegularExpression(misPropertyColumnConf.getMisPropertyConfigRegularExpression());

                // convert array list to object
                List<MisPropertyColumnConfCond>  columnConfConds =
                        (List<MisPropertyColumnConfCond>) misPropertyColumnConf.getColumnConfConds();
                ArrayList<MisPropertyColumnConfCond>  typeOneConds = new ArrayList<>();
                ArrayList<MisPropertyColumnConfCond>  typeTwoConds = new ArrayList<>();
                ArrayList<MisPropertyColumnConfCond>  typeThreeConds = new ArrayList<>();
                ArrayList<MisPropertyColumnConfCond>  typeFourConds = new ArrayList<>();

                for(MisPropertyColumnConfCond columnConfCond : columnConfConds){
                    if(columnConfCond.getMisPropertyColConfType().equals("1")){
                        typeOneConds.add(columnConfCond);
                    }
                    if(columnConfCond.getMisPropertyColConfType().equals("2")){
                        typeTwoConds.add(columnConfCond);
                    }
                    if(columnConfCond.getMisPropertyColConfType().equals("3")){
                        typeThreeConds.add(columnConfCond);
                    }
                    if(columnConfCond.getMisPropertyColConfType().equals("4")){
                        typeFourConds.add(columnConfCond);
                    }
                }

                LinkedHashMap<String, ArrayList<MisPropertyColumnConfCond>> propertyConfigDetail =
                        new LinkedHashMap<>();
                propertyConfigDetail.put("1", typeOneConds);
                propertyConfigDetail.put("2", typeTwoConds);
                propertyConfigDetail.put("3", typeThreeConds);
                propertyConfigDetail.put("4", typeFourConds);
                propertyColumnCondDto.setColumnConfigConditions(propertyConfigDetail);

                return propertyColumnCondDto;
            }
        }
        return  null;
    }


}

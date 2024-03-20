package com.asl.prd004.service.impl;

import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.utils.SerialNumberUtils;
import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.dao.MisTypeDao;
import com.asl.prd004.dao.ReportDao;
import com.asl.prd004.dao.ReportTemplateDao;
import com.asl.prd004.dto.StatisticsReportDTO;
import com.asl.prd004.entity.MisReport;
import com.asl.prd004.entity.MisReportTemplate;
import com.asl.prd004.entity.MisType;
import com.asl.prd004.service.IReportService;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements IReportService {
    @Autowired
    private ReportTemplateDao reportTemplateDao;

    @Autowired
    private ReportDao reportDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MisTypeDao typeDao;

    @Override
    public List<MisReportTemplate> getReportTemplate() {
        return reportTemplateDao.findAll();
    }

    @Override
    public PageDataDto getReports(PageableDto pageable) throws JSONException {
        Pageable page;

        if (StringUtils.isNotEmpty(pageable.getSortModel().getField())) {
            String sortField = pageable.getSortModel().getField();
            if (pageable.getSortModel().getSort().equalsIgnoreCase("asc")) {
                page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize(), Sort.by(sortField).ascending());
            } else {
                page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize(), Sort.by(sortField).descending());
            }
        } else {
            page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize());
        }
        Page<MisReport> reportPage = reportDao.findAll(page);
        List<MisReport> reportList = reportPage.getContent();
        PageDataDto reportDto = new PageDataDto();
        reportDto.setData(reportList);
        reportDto.setTotal(reportPage.getTotalElements());
        return reportDto;
    }

    @Override
    public Boolean generateReport(String templateId,String reportPath) {
        MisReportTemplate template = reportTemplateDao.findById(templateId).get();
        MisReport report = new MisReport();
        report.setMisReportId(SerialNumberUtils.getTableSequence("mis_report"));
        report.setMisReportName(template.getMisReportTempName());
        report.setMisReportDate(new Timestamp(System.currentTimeMillis()));
        report.setMisReportPath(reportPath);
        report.setMisReportUserId(ContextHolder.getUserId());
        reportDao.save(report);
        return true;
    }
    @Override
    public List<StatisticsReportDTO> getStatisticsReport(String dateFrom, String dateTo) {
        List<MisType> typeList = typeDao.findAll();
        List<StatisticsReportDTO> reportDTOList = new ArrayList<>();
        for (int i = 0; i < typeList.size(); i++) {
            StatisticsReportDTO reportDTO = new StatisticsReportDTO();
            reportDTO.setTableName(typeList.get(i).getMisTypeName());
            try{
                dateFrom = dateFrom.substring(0,10)+" 00:00:00";
                dateTo = dateTo.substring(0,10) +" 23:59:59";

                Query query = entityManager.createNativeQuery("select * from "+typeList.get(i).getMisTypeName()+"_s" +" where 1=1 and creation_date >='"+dateFrom +"' and creation_date<='"+dateTo+"'");
                List list = query.getResultList();
                reportDTO.setCounts(String.valueOf(list.size()));
            }catch (Exception e){
                reportDTO.setCounts("0");
            }
            reportDTOList.add(reportDTO);
        }
        return reportDTOList;
    }

    @Override
    public MisReport findById(String templateId) {
        return reportDao.findById(templateId).get();
    }

}

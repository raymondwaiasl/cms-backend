package com.asl.prd004.service;

import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.StatisticsReportDTO;
import com.asl.prd004.entity.MisReport;
import com.asl.prd004.entity.MisReportTemplate;
import org.json.JSONException;

import java.util.List;

public interface IReportService {
    List<MisReportTemplate> getReportTemplate();

    PageDataDto getReports(PageableDto page) throws JSONException;

    Boolean generateReport(String templateId,String reportPath);

    List<StatisticsReportDTO> getStatisticsReport(String dateFrom, String dateTo);

    MisReport findById(String templateId);
}

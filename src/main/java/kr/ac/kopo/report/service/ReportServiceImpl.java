package kr.ac.kopo.report.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.ac.kopo.report.dao.ReportMapper;
import kr.ac.kopo.report.dto.UserReportDTO;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;
    
    @Override
    public void submitReport(UserReportDTO report) {
        reportMapper.insertUserReport(report);
    }
}
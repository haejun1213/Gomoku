package kr.ac.kopo.report.service;

import kr.ac.kopo.report.dto.UserReportDTO;

public interface ReportService {
    void submitReport(UserReportDTO report);
}

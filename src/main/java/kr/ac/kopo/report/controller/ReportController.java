package kr.ac.kopo.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import kr.ac.kopo.report.dto.UserReportDTO;
import kr.ac.kopo.report.service.ReportService;
import java.util.Map;
import java.util.HashMap;

@Controller
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/api/report/submit")
    @ResponseBody
    public Map<String, Object> submitReport(@RequestBody UserReportDTO report) {
        Map<String, Object> response = new HashMap<>();
        try {
            reportService.submitReport(report);
            response.put("success", true);
            response.put("message", "신고가 정상적으로 접수되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "신고 접수 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        return response;
    }
}
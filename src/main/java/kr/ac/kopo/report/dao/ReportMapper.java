package kr.ac.kopo.report.dao;

import org.apache.ibatis.annotations.Mapper;
import kr.ac.kopo.report.dto.UserReportDTO;

@Mapper
public interface ReportMapper {
    void insertUserReport(UserReportDTO report);
}
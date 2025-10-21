package kr.ac.kopo.report.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class UserReportInfoDTO {
    private Long reportId;
    private Long reporterId;
    private String reporterNickname; // JOIN해서 가져올 신고자 닉네임
    private Long targetId;
    private String targetNickname;   // JOIN해서 가져올 피신고자 닉네임
    private String reason;
    private String status;
    private Date createdDatetime;
}
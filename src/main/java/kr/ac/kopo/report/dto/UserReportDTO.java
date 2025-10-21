package kr.ac.kopo.report.dto;

import lombok.Data;

@Data
public class UserReportDTO {
    private Long reporterId; // 신고자 ID
    private Long targetId;   // 피신고자 ID
    private String reason;   // 신고 사유 (채팅 내용)
}
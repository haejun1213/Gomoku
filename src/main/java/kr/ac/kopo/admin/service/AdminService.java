package kr.ac.kopo.admin.service;

import java.util.Map;

import kr.ac.kopo.common.dto.Criteria;
import kr.ac.kopo.user.dto.UserSearchCriteria;

public interface AdminService {
	// 표시할 땐 난 정지 뺴기
    Map<String, Object> getUserList(UserSearchCriteria cri);
    void changeUserStatus(Long userId, String status);
    
    // 불러올때 공개 비공개 다
    Map<String, Object> getAllRooms(Criteria cri);
    Map<String, Object> getReportList(Criteria cri);
    void processReport(Long reportId);
}
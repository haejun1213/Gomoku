package kr.ac.kopo.admin.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.ac.kopo.common.dto.Criteria;
import kr.ac.kopo.game.dto.GameRoomInfoDTO;
import kr.ac.kopo.report.dto.UserReportInfoDTO;
import kr.ac.kopo.user.dto.UserSearchCriteria;
import kr.ac.kopo.user.vo.UserVO;

@Mapper
public interface AdminMapper {
    List<UserVO> findUsersWithPaging(UserSearchCriteria cri);
    int countTotalUsers(UserSearchCriteria cri);
    void updateUserActiveStatus(@Param("userId") Long userId, @Param("isActive") String isActive);
    List<GameRoomInfoDTO> findAllRoomsWithPaging(Criteria cri);

    // ★ 전체 방 개수 조회 ★
    int countAllRooms();
    
    List<UserReportInfoDTO> findReportsWithPaging(Criteria cri);

    // 전체 신고 수 조회
    int countTotalReports();

    // 신고 상태 변경
    void updateReportStatus(@Param("reportId") Long reportId, @Param("status") String status);
    void deleteReport(Long reportId);
}
package kr.ac.kopo.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.ac.kopo.admin.dao.AdminMapper;
import kr.ac.kopo.common.dto.Criteria;
import kr.ac.kopo.common.dto.PageMaker;
import kr.ac.kopo.game.dto.GameRoomInfoDTO;
import kr.ac.kopo.report.dto.UserReportInfoDTO;
import kr.ac.kopo.user.dto.UserSearchCriteria;
import kr.ac.kopo.user.vo.UserVO;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Map<String, Object> getUserList(UserSearchCriteria cri) {
        Map<String, Object> resultMap = new HashMap<>();
        List<UserVO> userList = adminMapper.findUsersWithPaging(cri);
        
        PageMaker pageMaker = new PageMaker();
        pageMaker.setCri(cri);
        pageMaker.setTotalCount(adminMapper.countTotalUsers(cri));

        resultMap.put("userList", userList);
        resultMap.put("pageMaker", pageMaker);
        return resultMap;
    }

    @Override
    public void changeUserStatus(Long userId, String status) {
        adminMapper.updateUserActiveStatus(userId, status);
    }
    
    @Override
    public Map<String, Object> getAllRooms(Criteria cri) {
        Map<String, Object> resultMap = new HashMap<>();
        
        // 1. 페이징 UI를 만들기 위해 전체 방 개수를 가져옴
        PageMaker pageMaker = new PageMaker();
        pageMaker.setCri(cri);
        pageMaker.setTotalCount(adminMapper.countAllRooms());

        // 2. 현재 페이지에 해당하는 방 목록만 가져옴
        List<GameRoomInfoDTO> roomList = adminMapper.findAllRoomsWithPaging(cri);
        
        resultMap.put("allRooms", roomList);
        resultMap.put("pageMaker", pageMaker);
        
        return resultMap;
    }
    
    @Override
    public Map<String, Object> getReportList(Criteria cri) {
        Map<String, Object> resultMap = new HashMap<>();
        
        List<UserReportInfoDTO> reportList = adminMapper.findReportsWithPaging(cri);
        
        PageMaker pageMaker = new PageMaker();
        pageMaker.setCri(cri);
        pageMaker.setTotalCount(adminMapper.countTotalReports());

        resultMap.put("reportList", reportList);
        resultMap.put("pageMaker", pageMaker);
        return resultMap;
    }

    @Override
    public void processReport(Long reportId) {
        adminMapper.deleteReport(reportId);
    }
}
package kr.ac.kopo.admin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.ac.kopo.admin.service.AdminService;
import kr.ac.kopo.common.dto.Criteria;
import kr.ac.kopo.game.service.GameService;
import kr.ac.kopo.user.dto.UserSearchCriteria;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;


    @Autowired
    private GameService gameService;
    
    @GetMapping("/users")
    public String userList(UserSearchCriteria cri, Model model) {
        Map<String, Object> result = adminService.getUserList(cri);
        model.addAttribute("userList", result.get("userList"));
        model.addAttribute("pageMaker", result.get("pageMaker"));
        return "admin/userList";
    }

    @PostMapping("/users/update-status")
    @ResponseBody
    public Map<String, String> updateUserStatus(@RequestParam("userId") Long userId, @RequestParam("status") String status) {
        Map<String, String> response = new HashMap<>();
        try {
            adminService.changeUserStatus(userId, status);
            response.put("status", "success");
            response.put("message", "상태가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "상태 변경 중 오류가 발생했습니다.");
        }
        return response;
    }
    
    @GetMapping("/rooms")
    public String allRoomList(Criteria cri, Model model) { // Criteria 파라미터 추가
        Map<String, Object> result = adminService.getAllRooms(cri);
        model.addAttribute("allRooms", result.get("allRooms"));
        model.addAttribute("pageMaker", result.get("pageMaker"));
        // cri 객체도 모델에 추가하면, 검색 기능 유지 시 유용합니다.
        model.addAttribute("cri", cri); 
        return "admin/allRoomList";
    }
    
    @PostMapping("/rooms/delete")
    @ResponseBody
    public Map<String, Object> forceDeleteRoom(@RequestParam("roomId") int roomId) {
        Map<String, Object> response = new HashMap<>();
        try {
            gameService.deleteRoom(roomId);
            // 메모리에 남아있을 수 있는 방 정보도 제거 (웹소켓)
            // GameWebSocketHandler.removeRoom(roomId); <-- 이런 정적 메서드를 만들어 호출하면 더 좋음
            response.put("success", true);
            response.put("message", "방(ID: " + roomId + ")이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "방 삭제 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        return response;
    }
    
    @GetMapping("/reports")
    public String reportList(Criteria cri, Model model) {
        Map<String, Object> result = adminService.getReportList(cri);
        model.addAttribute("reportList", result.get("reportList"));
        model.addAttribute("pageMaker", result.get("pageMaker"));
        return "admin/reportList";
    }
    
    @PostMapping("/reports/delete")
    @ResponseBody
    public Map<String, Object> deleteReport(@RequestParam("reportId") Long reportId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 이 메서드는 AdminService에서 adminMapper.deleteReport(reportId)를 호출합니다.
            adminService.processReport(reportId);
            response.put("success", true);
            response.put("message", "신고 내역이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "삭제 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        return response;
    }
    
}
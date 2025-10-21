package kr.ac.kopo.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import kr.ac.kopo.common.dto.Criteria;
import kr.ac.kopo.user.dto.UserStatsDTO;
import kr.ac.kopo.user.service.UserService;
import kr.ac.kopo.user.vo.UserVO;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile/{userId}")
    public String viewUserProfile(@PathVariable("userId") Long userId, HttpSession session, Model model, Criteria cri) {
        UserVO loginUser = (UserVO) session.getAttribute("loggedInUser");
        if (loginUser == null) {
            // 비로그인 시, 프로필을 보려면 로그인이 필요하다고 안내 후 로그인 페이지로
            return "redirect:/login";
        }
        
        // 보려는 프로필의 유저 정보를 DB에서 조회
        UserVO profileUser = userService.findUserById(userId);
        if (profileUser == null) {
            // 해당 유저가 없으면 에러 페이지 또는 메인으로
            return "redirect:/main";
        }
        
        // 마이페이지와 동일하게 통계 및 대전 기록을 조회
        UserStatsDTO stats = userService.getUserStats(userId);
        Map<String, Object> historyMap = userService.getGameHistoryWithPaging(userId, cri);

        // 조회한 정보를 모델에 담아 JSP로 전달
        model.addAttribute("profileUser", profileUser); // 조회 대상 유저 정보
        model.addAttribute("stats", stats);
        model.addAttribute("historyList", historyMap.get("historyList"));
        model.addAttribute("pageMaker", historyMap.get("pageMaker"));
        
        return "profile/view"; // /WEB-INF/views/profile/view.jsp
    }
    
    
    @GetMapping("/api/profile/{userId}")
    @ResponseBody // 이 어노테이션이 중요! JSP가 아닌 JSON 데이터를 반환하게 함
    public Map<String, Object> getUserProfileData(@PathVariable("userId") Long userId) {
        Map<String, Object> responseData = new HashMap<>();

        try {
            UserVO profileUser = userService.findUserById(userId);
            UserStatsDTO stats = userService.getUserStats(userId);
            
            // 비밀번호 등 민감 정보는 제외하고 전달
            profileUser.setPassword(null);

            responseData.put("success", true);
            responseData.put("profileUser", profileUser);
            responseData.put("stats", stats);
            // 필요하다면 최근 게임 기록 일부도 여기에 추가할 수 있습니다.
            
        } catch (Exception e) {
            responseData.put("success", false);
            responseData.put("message", "사용자 정보를 불러오는데 실패했습니다.");
        }
        
        return responseData;
    }
}
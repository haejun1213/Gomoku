// 예시: MyPageController.java
package kr.ac.kopo.user.controller; // 적절한 패키지 경로

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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kr.ac.kopo.common.dto.Criteria;
import kr.ac.kopo.user.dto.UserStatsDTO;
import kr.ac.kopo.user.service.UserService;
import kr.ac.kopo.user.vo.UserVO; // UserVO import

@Controller
@RequestMapping("/mypage")
public class MyPageController {

	@Autowired
	private UserService userService;

	@GetMapping("")
	public String myPage(HttpSession session, Model model, Criteria cri) { // Criteria 파라미터 추가
		UserVO loginUser = (UserVO) session.getAttribute("loggedInUser");
		if (loginUser == null) {
			return "redirect:/login";
		}

		UserStatsDTO stats = userService.getUserStats(loginUser.getUserId());

		// 서비스로부터 기록 목록과 페이지 정보를 함께 받아옴
		Map<String, Object> historyMap = userService.getGameHistoryWithPaging(loginUser.getUserId(), cri);

		model.addAttribute("stats", stats);
		model.addAttribute("historyList", historyMap.get("historyList"));
		model.addAttribute("pageMaker", historyMap.get("pageMaker"));

		return "mypage/main";
	}

	@GetMapping("/edit")
    public String showEditProfileForm(HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute("loggedInUser");
        if (loginUser == null) return "redirect:/login";
        
        // 게스트 유저는 프로필 수정 불가
        if ("G".equals(loginUser.getRole())) {
            return "redirect:/mypage";
        }
        return "mypage/editProfile";
    }

	@PostMapping("/edit")
    public String processEditProfile(HttpSession session, RedirectAttributes rttr,
                                     @RequestParam("nickname") String nickname,
                                     @RequestParam(value = "currentPassword", required = false) String currentPassword,
                                     @RequestParam(value = "newPassword", required = false) String newPassword,
                                     @RequestParam(value = "confirmNewPassword", required = false) String confirmNewPassword,
                                     @RequestParam("profileImageFile") MultipartFile profileImageFile) {
        
        UserVO loginUser = (UserVO) session.getAttribute("loggedInUser");
        
        try {
            // 서비스 호출 시 UserVO 객체를 통째로 넘겨 소셜 로그인 여부 판단
            String newProfileImageUrl = userService.updateProfile(loginUser, nickname, currentPassword, 
                                                                  newPassword, confirmNewPassword, profileImageFile);
            
            // 세션에 있는 UserVO 객체의 정보를 직접 업데이트
            loginUser.setNickname(nickname);
            if (newProfileImageUrl != null) {
                loginUser.setProfileImage(newProfileImageUrl);
            }
            session.setAttribute("loggedInUser", loginUser);
            
            rttr.addFlashAttribute("successMessage", "프로필이 성공적으로 수정되었습니다.");
            return "redirect:/mypage";

        } catch (Exception e) {
            rttr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/mypage/edit";
        }
    }
	
	@PostMapping("/delete-account")
    @ResponseBody
    public Map<String, Object> deleteAccount(HttpSession session, @RequestParam("password") String password) {
        UserVO loginUser = (UserVO) session.getAttribute("loggedInUser");
        Map<String, Object> response = new HashMap<>();

        if (loginUser == null) {
            response.put("success", false);
            response.put("message", "로그인 정보가 없습니다.");
            return response;
        }

        try {
            userService.deleteUserAccount(loginUser.getUserId(), password);
            session.invalidate(); // 탈퇴 성공 시 즉시 로그아웃 처리
            
            response.put("success", true);
            response.put("message", "회원 탈퇴가 완료되었습니다. 이용해주셔서 감사합니다.");
            response.put("redirectUrl", "/Gomoku/"); // 메인 페이지 경로

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

	@GetMapping("/delete-account")
	public String deleteAccount() {
		// 계정 탈퇴 로직 (POST 요청으로 처리하는 것이 더 안전)
		// 실제로는 POST 요청 + 비밀번호 재확인 등의 절차 필요
		return "redirect:/logout"; // 탈퇴 후 로그아웃
	}

	@GetMapping("/replay/all")
	public String viewAllReplays(@SessionAttribute(name = "loggedInUser", required = false) UserVO loggedInUser,
			Model model) {
		// 모든 게임 기록 조회 및 전달 로직
		// List<GameHistoryVO> allHistories =
		// gameService.getAllGameHistories(loggedInUser.getUserId());
		// model.addAttribute("allHistories", allHistories);
		return "allReplays"; // 모든 게임 기록 목록 페이지
	}
}
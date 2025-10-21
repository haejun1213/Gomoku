package kr.ac.kopo.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import kr.ac.kopo.user.dto.UserLoginDTO;
import kr.ac.kopo.user.service.KakaoAuthService;
import kr.ac.kopo.user.service.UserService;
import kr.ac.kopo.user.vo.KakaoProfile;
import kr.ac.kopo.user.vo.KakaoToken;
import kr.ac.kopo.user.vo.UserVO;

@Controller
public class KakaoLoginController {

    @Autowired
    private KakaoAuthService kakaoAuthService;

    @Autowired
    private UserService userService;

    // 카카오 로그인 콜백 처리
    @RequestMapping(value = "/kakao/callback", method = RequestMethod.GET)
    public String kakaoCallback(@RequestParam("code") String code, Model model, HttpSession session) {
        try {
            // 1. 인가 코드로 액세스 토큰 요청
            KakaoToken kakaoToken = kakaoAuthService.getKakaoAccessToken(code);
            if (kakaoToken == null || kakaoToken.getAccessToken() == null) {
                throw new Exception("카카오 액세스 토큰을 가져오는데 실패했습니다.");
            }

            // 2. 액세스 토큰으로 사용자 정보 요청
            KakaoProfile kakaoProfile = kakaoAuthService.getKakaoUserInfo(kakaoToken.getAccessToken());
            if (kakaoProfile == null || kakaoProfile.getId() == null) {
                throw new Exception("카카오 사용자 정보를 가져오는데 실패했습니다.");
            }

            // 3. 사용자 정보로 로그인 또는 회원가입 처리, 나중에 구글이나 네이버 로그인 위해 타ㅏ입
            String socialType = "KAKAO";
            String socialId = String.valueOf(kakaoProfile.getId());
            String nickname = kakaoProfile.getKakaoAccount()!= null && kakaoProfile.getKakaoAccount().getProfile()!= null
                            ? kakaoProfile.getKakaoAccount().getProfile().getNickname() : null;
            String email = kakaoProfile.getKakaoAccount()!= null? kakaoProfile.getKakaoAccount().getEmail() : null;
            String profileImage = kakaoProfile.getKakaoAccount()!= null && kakaoProfile.getKakaoAccount().getProfile()!= null
                                ? kakaoProfile.getKakaoAccount().getProfile().getProfileImageUrl() : null;

            UserVO loggedInUser = userService.processSocialLogin(socialType, socialId, nickname, email, profileImage);

            // 세션에 사용자 정보 저장
            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("kakaoAccessToken", kakaoToken.getAccessToken());

            return "redirect:/main"; // 로그인 성공 시 메인 페이지로 리다이렉트
        } catch (Exception e) {
            model.addAttribute("errorMessage", "카카오 로그인 실패: " + e.getMessage());
            model.addAttribute("userLoginDTO", new UserLoginDTO());
            return "login"; // 로그인 실패 시 로그인 페이지로 이동
        }
    }
}
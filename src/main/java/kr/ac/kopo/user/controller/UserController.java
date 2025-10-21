package kr.ac.kopo.user.controller; // 패키지 경로 변경

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.ac.kopo.user.dto.UserLoginDTO;
import kr.ac.kopo.user.service.KakaoAuthService;
import kr.ac.kopo.user.service.UserService; // import 경로 변경
import kr.ac.kopo.user.vo.AjaxResponse; // import 경로 변경
import kr.ac.kopo.user.vo.UserVO;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private KakaoAuthService kakaoAuthService; // 추가
    // 메인 페이지 (index.jsp로 리다이렉트)
    @RequestMapping("/")
    public String home() {
        return "redirect:/index";
    }

    // index.jsp 페이지 표시
    @RequestMapping("/index")
    public String showIndex() {
        return "index";
    }

    // 회원가입 폼 표시 [13, 14, 15, 16, 17]
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView showRegistrationForm() {
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("user", new UserVO()); // 빈 User 객체를 폼에 바인딩 [14, 17]
        return mav;
    }

    // 회원가입 처리 [14, 18, 19, 15, 16, 17]
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processRegistration(@Valid @ModelAttribute("user") UserVO user, BindingResult result, Model model, RedirectAttributes rttr) {
        // Bean Validation 오류 확인 [18, 19, 17]
        if (result.hasErrors()) {
            return "register"; // 오류가 있으면 다시 폼으로 [18, 19, 17]
        }

        try {
            // 이메일 및 닉네임 중복 확인 (서비스 계층에서 처리)
            userService.registerUser(user);
            model.addAttribute("message", "회원가입이 성공적으로 완료되었습니다. 로그인해주세요.");
            model.addAttribute("userLoginDTO", new UserLoginDTO()); // <-- 이 줄을 추가합니다.

            return "login"; // 성공 시 로그인 페이지로 이동
        } catch (Exception e) {
        	rttr.addFlashAttribute("errorMessage", e.getMessage());
            return "register"; // 중복 오류 시 다시 폼으로
        }
    }

    // 이메일 중복 확인 (AJAX) [19, 20, 21, 22, 23, 24]
    @RequestMapping(value = "/checkEmail", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResponse checkEmailDuplication(@RequestParam("email") String email) {
        boolean isDuplicated = userService.isEmailDuplicated(email);
        if (isDuplicated) {
            return new AjaxResponse(false, "이미 존재하는 이메일입니다.");
        } else {
            return new AjaxResponse(true, "사용 가능한 이메일입니다.");
        }
    }

    // 닉네임 중복 확인 (AJAX) [20, 21, 22, 23, 24]
    @RequestMapping(value = "/checkNickname", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResponse checkNicknameDuplication(@RequestParam("nickname") String nickname) {
        boolean isDuplicated = userService.isNicknameDuplicated(nickname);
        if (isDuplicated) {
            return new AjaxResponse(false, "이미 사용 중인 닉네임입니다.");
        } else {
            return new AjaxResponse(true, "사용 가능한 닉네임입니다.");
        }
    }

    // 로그인 폼 표시 [14, 15, 16, 17]
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView showLoginForm() {
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("userLoginDTO", new UserLoginDTO()); // 로그인 폼에 바인딩할 DTO
        return mav;
    }

    // 로그인 처리 (전통적인 방식) [14, 18, 19, 15, 16, 17]
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String processLogin(@Valid @ModelAttribute("userLoginDTO") UserLoginDTO userLoginDTO, BindingResult result, Model model, HttpSession session, RedirectAttributes rttr) {
        if (result.hasErrors()) {
            return "login"; // 유효성 검사 오류 시 다시 폼으로
        }

        try {
        	UserVO loggedInUser = userService.loginUser(userLoginDTO.getEmail(), userLoginDTO.getPassword());
            session.setAttribute("loggedInUser", loggedInUser); // 세션에 사용자 정보 저장
            
            String destination = (String) session.getAttribute("destination");
            session.removeAttribute("destination");
            
            return "redirect:" + (destination != null ? destination : "/main"); // 로그인 성공 시 메인 페이지로 리다이렉트
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "login"; // 로그인 실패 시 오류 메시지와 함께 다시 폼으로
        }
    }

    // 비회원으로 시작 (게스트 로그인)
    @RequestMapping(value = "/guest", method = RequestMethod.GET)
    public String startAsGuest(HttpSession session) {
        try {
        	UserVO guestUser = userService.createGuestUser();
            session.setAttribute("loggedInUser", guestUser); // 게스트 사용자 정보 세션에 저장
            
            String destination = (String) session.getAttribute("destination");
            session.removeAttribute("destination");
            
            return "redirect:" + (destination != null ? destination : "/main"); // 메인 페이지로 리다이렉트
        } catch (Exception e) {
            // 게스트 생성 실패 시 로그인 페이지로 리다이렉트 또는 오류 메시지 표시
            return "redirect:/login?error=guest_creation_failed";
        }
    }

    // 로그아웃
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        UserVO loggedInUser = (UserVO) session.getAttribute("loggedInUser");

        if (loggedInUser != null && "KAKAO".equals(loggedInUser.getSocialType())) {
            String kakaoAccessToken = (String) session.getAttribute("kakaoAccessToken");
            if (kakaoAccessToken != null) {
                try {
                    kakaoAuthService.kakaoUnlink(kakaoAccessToken);
                } catch (Exception e) {
                    System.err.println("카카오 연결 끊기 중 오류 발생: " + e.getMessage());
                    // 오류가 발생해도 로그아웃 자체는 진행
                }
            }
        }

        session.invalidate(); // 우리 서비스 세션 무효화
        return "redirect:/index"; // 메인 페이지로 리다이렉트
    }

    // 로그인 후 메인 페이지 (예시)
    @RequestMapping("/main")
    public String mainPage(HttpSession session, Model model) {
    	UserVO user = (UserVO) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login"; // 로그인되지 않았으면 로그인 페이지로
        }
        model.addAttribute("user", user);
        return "main"; // main.jsp 뷰 반환
    }
    
 // 쿠키를 이용한 게스트 로그인 처리
    @GetMapping("/guest-login")
    public String guestLogin(
            @CookieValue(value = "guest_nickname", required = false) String nickname, 
            HttpSession session, 
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        UserVO guestUser = null;

        // 1. 쿠키에 저장된 닉네임이 있는지 확인
        if (nickname != null) {
            // 쿠키가 있으면, 해당 닉네임으로 DB에서 유저 정보를 찾아봄
            guestUser = userService.findGuestByNickname(nickname);
        }

        try {
            // 2. 쿠키가 없었거나, 쿠키는 있지만 DB에서 해당 유저가 삭제된 경우 -> 새로운 게스트 생성
            if (guestUser == null) {
                guestUser = userService.createGuestUser();
            }

            // ★★★ 핵심 수정 부분 ★★★
            // 3. 새로운 게스트든, 기존 게스트든 로그인 시 항상 쿠키를 새로 발급/갱신합니다.
            Cookie guestCookie = new Cookie("guest_nickname", guestUser.getNickname());
            
            // 유효기간을 매우 길게 설정합니다. (예: 1년)
            // 365일 * 24시간 * 60분 * 60초
            guestCookie.setMaxAge(365 * 24 * 60 * 60);
            
            guestCookie.setPath("/"); // 웹사이트 전체 경로에서 쿠키가 유효하도록 설정
            response.addCookie(guestCookie); // 응답에 쿠키를 추가하여 브라우저에 전송
            // ★★★ 수정 끝 ★★★

            // 4. 세션에 게스트 유저 정보 저장
            session.setAttribute("loggedInUser", guestUser);
            
            // 5. 원래 가려던 목적지가 있으면 그곳으로, 없으면 메인으로 이동
            String destination = (String) session.getAttribute("destination");
            session.removeAttribute("destination");
            
            return "redirect:" + (destination != null ? destination : "/main");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "게스트 로그인에 실패했습니다: " + e.getMessage());
            return "redirect:/login"; // 실패 시 로그인 페이지로
        }
    }
}
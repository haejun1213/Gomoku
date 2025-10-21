package kr.ac.kopo.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import kr.ac.kopo.user.service.UserService;
import kr.ac.kopo.user.vo.UserVO;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class FaceController {

    @Autowired
    private UserService userService;

    
    @GetMapping("/face-login")
    public String showFaceLoginPage() {
        return "faceLogin";
    }
    
    @PostMapping("/face/register")
    @ResponseBody
    public Map<String, Object> registerFace(HttpSession session, @RequestParam("imageDataUrl") String imageDataUrl) {
        UserVO loginUser = (UserVO) session.getAttribute("loggedInUser");
        Map<String, Object> response = new HashMap<>();
        try {
            userService.registerUserFace(loginUser.getUserId(), imageDataUrl);
            response.put("success", true);
            response.put("message", "얼굴이 성공적으로 등록되었습니다!");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @PostMapping("/face/login")
    @ResponseBody
    public Map<String, Object> loginWithFace(@RequestParam("email") String email, 
                                             @RequestParam("imageDataUrl") String imageDataUrl,
                                             HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserVO loggedInUser = userService.loginWithFace(email, imageDataUrl);
            session.setAttribute("loggedInUser", loggedInUser);
            
            String destination = (String) session.getAttribute("destination");
            session.removeAttribute("destination");
            
            response.put("success", true);
            response.put("redirectUrl", (destination != null ? destination : "/main"));
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
}
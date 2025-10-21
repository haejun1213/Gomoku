package kr.ac.kopo.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.ac.kopo.user.vo.UserVO;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        HttpSession session = request.getSession();
        UserVO loginUser = (UserVO) session.getAttribute("loggedInUser");

        // 1. 로그인을 안했거나, 2. 관리자(A)가 아니면
        if (loginUser == null || !"A".equals(loginUser.getRole())) {
            // "접근 권한이 없습니다." 메시지를 리다이렉트 페이지에 전달
            FlashMap flashMap = new FlashMap();
            flashMap.put("errorMessage", "접근 권한이 없습니다.");
            RequestContextUtils.getOutputFlashMap(request).putAll(flashMap);
            
            // 메인 페이지로 리다이렉트
            response.sendRedirect(request.getContextPath() + "/main");
            return false; // 원래의 컨트롤러 실행 중단
        }
        
        // 관리자면 통과
        return true;
    }
}
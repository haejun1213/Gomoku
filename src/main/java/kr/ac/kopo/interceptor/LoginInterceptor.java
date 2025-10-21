package kr.ac.kopo.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        HttpSession session = request.getSession();
        Object loggedInUser = session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            // 1. 로그인 되어 있으면, 그대로 진행
            return true;
        } else {
            // 2. 로그인 되어 있지 않으면
            
            // 2-1. 원래 가려던 목적지 주소를 저장
        	String requestUri = request.getRequestURI(); // 예: /Gomoku/game/play/81
            String contextPath = request.getContextPath(); // 예: /Gomoku
            
            // 전체 경로에서 contextPath 부분을 제외한 순수 경로만 저장
            String destination = requestUri.substring(contextPath.length()); // 결과: /game/play/81
            
            String queryString = request.getQueryString();
            if (queryString != null) {
                destination += "?" + queryString;
            }
            session.setAttribute("destination", destination);
            // 2-2. 로그인 페이지로 리다이렉트
            response.sendRedirect(request.getContextPath() + "/login");
            return false; // 원래의 컨트롤러 실행 중단
        }
    }
}
package kr.ac.kopo.game.websocket;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class WebSocketConfig extends ServerEndpointConfig.Configurator implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        WebSocketConfig.context = applicationContext;
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        // Spring 컨테이너에서 WebSocket 핸들러 Bean을 가져오도록 설정 (의존성 주입을 위함)
        return context.getBean(endpointClass);
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        // WebSocket 연결 시도 시, 기존의 HttpSession을 가져와 WebSocket 세션의 속성으로 저장
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        if (httpSession != null) {
            sec.getUserProperties().put("httpSession", httpSession);
        }
    }
}
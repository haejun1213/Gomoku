package kr.ac.kopo.user.service; // 패키지 경로 변경

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.kopo.user.vo.KakaoProfile; // import 경로 변경
import kr.ac.kopo.user.vo.KakaoToken; // import 경로 변경
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoAuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${kakao.rest.api.key}")
    private String KAKAO_REST_API_KEY;

    @Value("${kakao.redirect.uri}")
    private String KAKAO_REDIRECT_URI;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 인가 코드로 액세스 토큰 요청 [2]
    public KakaoToken getKakaoAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // [3, 4, 5]

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_REST_API_KEY);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );
            return objectMapper.readValue(response.getBody(), KakaoToken.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 액세스 토큰으로 사용자 정보 요청 [6, 7, 8, 9, 10, 2, 11, 12]
    public KakaoProfile getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken); // [9, 10, 2, 11]
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 또는 MediaType.APPLICATION_JSON

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    request,
                    String.class
            );
            return objectMapper.readValue(response.getBody(), KakaoProfile.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void kakaoUnlink(String accessToken) throws Exception {
        // 연결 끊기 요청 URL
        String unlinkUrl = "https://kapi.kakao.com/v1/user/unlink";

        // HTTP Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Entity 생성 (body는 없거나 비어있을 수 있음)
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        // RestTemplate으로 API 호출
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
            unlinkUrl,
            HttpMethod.POST,
            request,
            String.class
        );

        // 응답 처리 (필요에 따라 성공 여부 로깅 등)
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("카카오 연결 끊기 성공: " + response.getBody());
        } else {
            throw new Exception("카카오 연결 끊기 실패: " + response.getStatusCode() + ", " + response.getBody());
        }
    }
}
package com.example.JoinWeb.login.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class KakaoController {
    // (연습용 최소 보안) 키/URI만 외부화. 서비스/DTO 분리 없이 컨트롤러 하나로 처리
    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final RestTemplate rest = new RestTemplate();

    /**
     * (연습용) 카카오 코드 교환 → 닉네임만 반환
     * 프론트에서 { code }를 보내면,
     * 1) 토큰 교환
     * 2) 사용자 정보 조회
     * 3) nickname만 리턴
     */
    @PostMapping("/kakao")
    public ResponseEntity<?> kakao(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        if (!StringUtils.hasText(code)) {
            return ResponseEntity.badRequest().body("code 누락");
        }

        try {
            // 1) 토큰 교환 (x-www-form-urlencoded)
            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "authorization_code");
            form.add("client_id", clientId);
            form.add("redirect_uri", redirectUri);
            form.add("code", code);

            HttpEntity<MultiValueMap<String, String>> tokenReq = new HttpEntity<>(form, tokenHeaders);

            ResponseEntity<Map> tokenRes = rest.postForEntity(
                    "https://kauth.kakao.com/oauth/token", tokenReq, Map.class);

            if (!tokenRes.getStatusCode().is2xxSuccessful() || tokenRes.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("토큰 교환 실패");
            }

            String accessToken = (String) tokenRes.getBody().get("access_token");
            if (!StringUtils.hasText(accessToken)) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("access_token 없음");
            }

            // 2) 사용자 정보 조회 (닉네임만 필요)
            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(accessToken);

            HttpEntity<Void> userReq = new HttpEntity<>(userHeaders);
            ResponseEntity<Map> userRes = rest.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    userReq,
                    Map.class
            );

            if (!userRes.getStatusCode().is2xxSuccessful() || userRes.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("사용자 정보 요청 실패");
            }

            // 3) JSON(Map)에서 nickname만 꺼냄 (DTO 없이 단순 Map 접근)
            // 구조: kakao_account -> profile -> nickname
            Map bodyMap = userRes.getBody();
            Map kakaoAccount = (Map) bodyMap.get("kakao_account");
            Map profile = kakaoAccount != null ? (Map) kakaoAccount.get("profile") : null;
            String nickname = profile != null ? (String) profile.get("nickname") : null;

            // (연습용) 닉네임만 반환. 세션/JWT 등은 생략.
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("nickname", nickname != null ? nickname : "KakaoUser");
            return ResponseEntity.ok(result);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("카카오 연동 중 오류");
        }
    }
}

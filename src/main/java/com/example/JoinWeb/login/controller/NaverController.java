package com.example.JoinWeb.login.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class NaverController {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.redirect-uri}")
    private String redirectUri;

    private final RestTemplate rest = new RestTemplate();

    @PostMapping("/naver")
    public ResponseEntity<?> naver(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        String state = body.get("state");

        if (!StringUtils.hasText(code)) {
            return ResponseEntity.badRequest().body("code 누락");
        }

        if (!StringUtils.hasText(state)) {
            return ResponseEntity.badRequest().body("state 누락");
        }

        if (!StringUtils.hasText(clientId) || !StringUtils.hasText(clientSecret)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("네이버 client-id 또는 client-secret이 설정되지 않았습니다.");
        }

        try {
            String tokenUrl = UriComponentsBuilder
                    .fromUriString("https://nid.naver.com/oauth2.0/token")
                    .queryParam("grant_type", "authorization_code")
                    .queryParam("client_id", clientId)
                    .queryParam("client_secret", clientSecret)
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("code", code)
                    .queryParam("state", state)
                    .toUriString();

            ResponseEntity<Map> tokenRes = rest.getForEntity(tokenUrl, Map.class);
            if (!tokenRes.getStatusCode().is2xxSuccessful() || tokenRes.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("네이버 토큰 교환 실패");
            }

            String accessToken = (String) tokenRes.getBody().get("access_token");
            if (!StringUtils.hasText(accessToken)) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("네이버 access_token 없음");
            }

            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(accessToken);
            HttpEntity<Void> userReq = new HttpEntity<>(userHeaders);

            ResponseEntity<Map> userRes = rest.exchange(
                    "https://openapi.naver.com/v1/nid/me",
                    HttpMethod.GET,
                    userReq,
                    Map.class
            );

            if (!userRes.getStatusCode().is2xxSuccessful() || userRes.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("네이버 사용자 정보 요청 실패");
            }

            Map bodyMap = userRes.getBody();
            Map response = (Map) bodyMap.get("response");
            String nickname = response != null ? (String) response.get("nickname") : null;

            Map<String, Object> result = new HashMap<>();
            String safeNickname = StringUtils.hasText(nickname) ? nickname : "NaverUser";
            result.put("nickname", safeNickname);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("네이버 연동 중 오류");
        }
    }
}

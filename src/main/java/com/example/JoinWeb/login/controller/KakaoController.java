package com.example.JoinWeb.login.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class KakaoController {
    // (연습용 최소 구현) 키/URI만 외부화. 서비스/DTO 분리 없이 컨트롤러 하나로 처리
    @Value("${kakao.client-id}")
    private String clientId;

    // 스프링 환경에서 kakao.redirect-uri라는 프로퍼티 값을 찾아서 redirectUri에 주입한다.
    // application.properties에 적어뒀다.
    // Q. 토큰도 vue에 있는 code로 받는지 확인 질문 필요.
    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    // 레스트템플레이트 객체 생성
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
        // StringUtils.hasText는 문자열 유효성 검증 유틸 메서드이다.
        // String을 파라미터로 전달하면, null 체크도 해주고 길이가 0은 아닌지, 공백이 아닌 문자가 포함되었는지 체크도 해준다.
        // * hasLength와 hasText 차이에 대해 알아두면 좋을 것 같다.
        if (!StringUtils.hasText(code)) {
            return ResponseEntity.badRequest().body("code 누락");
        }

        try {
            // 1) 토큰 교환 (요청을 이렇게 하도록 카카오에서 지정함 x-www-form-urlencoded)
            // 헤더의 컨텐트 타입이 x-www-form-urlencoded라서 MediaType을 아래와 같이 지정했다.
            // 인코딩을 x-www-form-urlencoded로 한 것은 HTML의 form 태그에서 서버에 전송할 때 주로 사용되는 방식이다.
            // FormHttpMessageConverter를 사용하여 파싱한다. 그리고 HTTP 요청 데이터는 MultiValueMap 형태로 반환된다.
            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // MultiValueMap은 자바에서 제공하는 것이 아닌 spring에서 제공하는 인터페이스
            // Map 인터페이스를 상속할 때 Value의 값을 List로 감싼채로 상속받는다.
            // 하나의 key에 하나 이상의 value로 이루어진 리스트를 쌍으로 받는다.
            // key=value&key=value.. 형태에 MultiValueMap은 아주 유용하다.
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "authorization_code");
            form.add("client_id", clientId);
            form.add("redirect_uri", redirectUri);
            form.add("code", code);

            // 위에 설정한 것들을 담아 토큰 요청을 할 객체를 만들었다.
            // APPLICATION_FORM_URLENCODED는 폼형태로 url인코딩한다는건데, 이 말은 바디가 어떻게 생겼는지
            // 헤더 컨텐트 타입에 명시해야한다. 그래서 HttpEntity를 사용했다. HttpEntity는 바디와 헤더 컨탠트 타입을 함께 보내기 때문에
            // 현재 사용하고 있는 폼 형태에 맞춰 사용하기 아주 유용하다.
            // 바디에는 키=값&키=값이 들어가고, 헤더 컨텐트 타입에는 폼형식이 들어간다.
            HttpEntity<MultiValueMap<String, String>> tokenReq = new HttpEntity<>(form, tokenHeaders);


            // OAuth 2.0 토큰 엔드포인트는 명세상 POST + application/x-www-form-urlencoded 요청을 요구힌다.
            // 그래서 tokenReq에 x-www-form-urlencode담고 postForEntity로 보냈다.
            // postForEntity는 POST 요청을 보내고, 상태/헤더/바디를 한꺼번에 받고 싶을 때 쓰는 편의 메서드이다.
            // 응답으로 받아온 tokenRes를 Map.class를 적어 역지렬화한다.
            // 카카오에서 보내온 응답은 Json 형태이다. 그걸 Map으로 역직렬화를 뜻이다.
            // 그래서 ResponseEntity가 <Map> 타입을 가지고 있는 것이다.
            // rest는 RestTemplate로 MessageConverter를 이용해 java 오브젝트를 요청 바디에 담을 메시지로 변환한다.
            // 이때 메시지 형태는 상황에 따라 다르다. postForEntity가 restTemplate에서 지원하는 메서드이다.
            ResponseEntity<Map> tokenRes = rest.postForEntity(
                    "https://kauth.kakao.com/oauth/token", tokenReq, Map.class);

            // tokenRes의 받아온 상태 코드가 성공적이지 않거나, 받아온 바디가 널인 경유 오류를 프론트에 전달한다.
            if (!tokenRes.getStatusCode().is2xxSuccessful() || tokenRes.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("토큰 교환 실패");
            }

            // tokenRes의 바디에서 access_token에 들어있는 정보를 가져왔다.
            // 이 accessToken의 문자열 유효성 검증이 False이면 오류를 프론트에 전달한다.
            String accessToken = (String) tokenRes.getBody().get("access_token");
            if (!StringUtils.hasText(accessToken)) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("access_token 없음");
            }


            // Q. WebClient를 사용하지 않은 이유? 비동기 방식이니까 더 나은 것 아닌가?


            // 2) 사용자 정보 조회 (닉네임만 필요, 일단 동의한 거 다 받아옴)
            HttpHeaders userHeaders = new HttpHeaders();
            // Authorization 헤더에 Bearer 토큰 추가
            // Authorization은 요청을 보낸 주체와 접근 권한이 있다는 걸 서버에 증명할 때 쓰는 표준 HTTP 헤더
            // Bearer 토큰은 토큰을 가진 사람(Bearer)이면 누구든 사용할 수 있는 형태의 엑세스 토큰 타입
            // 그래서 카카오가 발급해준 접근 권한 증표인 access 토큰을 반드시 헤더에 붙여야한다.
            userHeaders.setBearerAuth(accessToken);

            // 토큰을 교환할 땐 바디에 키=값&키=값...이 들어갔다.
            // 토큰으로 정보를 교환하기 위해서 헤더에 받은 토큰값만 보내면 되니까 HttpEntity <Void>로 보낸다.
            // 근데 그런거면 RequestEntity를 써도 되지 않나? 써도 되나, 둘 사이엔 차이가 존재한다.
            // 요약하면 HttpEntity는 간단히 헤더/바디만 묶고 싶을 때, ResponseEntity는 요청 전체를 선언형으로 한 줄에 표현하고자 할 때 사용한다.
            // RequestEntity는 메서드+URL+헤더+바디(요청 전부)를 담는다. 또, exchange만 사용할 수 있다.
            // 그리고 HttpEntity는 헤더+바디를 담는다.
            HttpEntity<Void> userReq = new HttpEntity<>(userHeaders);

            // HTTP 요청 및 응답 처리로 exchange는 rest 템플렛에서 제공하는 메서드이다.
            // 이때 exchange는 헤더를 생성하고 모든 요청 방법을 허용한다.
            // 아까 토큰을 Authorization을 헤더에 담았다.(HttpEntity로) 이런 형태를 GET에 넣을 방법이 없다.
            // 그래서 exchange를 사용하여 모든 요청 방법을 허용하도록 한 것이다.
            // exchange(url, HttpMethod, HttpEntity, responseType) 이렇게 생겨서, HTTP 메서드와 헤더/바디 모두 직접 지정 가능하다.
            // HttpEntity를 담을 수 있는 세번째 인자에 담는다!
            // getForEntity(url, responseType)는 HttpEntity를 담을 곳이 없다.

            // Map으로 역직렬화 한 이유는 키/값으로 파싱하기 위함이다.
            // 원래는 DTO 클래스로 받는 것이 더 안전하다. 그러나 연습 코드이고, 빠르게 값을 뽑아 쓰기 위함이라 Map 타입으로 설정했다.
            ResponseEntity<Map> userRes = rest.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    // HTTPMethod.GET으로 받은 닉네임을 URL에 담기는게 아니라 HTTPS 응답 바디에 담긴다.
                    // 카카오 API 서버와 백엔드 서버 간 요청이기 때문에 URl에 담길 수도 없다.
                    // 그리고 통신은 HTTPS이기 때문에 전송 중 도청 방지가 된다.(콘솔에 찍히거나, 프론트로 보내는 도중 노출 위험이 생길 순 있다)
                    // GET은 카카오가 요청한 방식임
                    HttpMethod.GET,
                    userReq,
                    Map.class
            );

            // 상태 코드가 2로 시작하는 코드가 아니라면 혹은 바디값이 null이면 False
            if (!userRes.getStatusCode().is2xxSuccessful() || userRes.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("사용자 정보 요청 실패");
            }

            // 3) JSON(Map)에서 nickname만 꺼냄 (DTO 없이 단순 Map 접근)
            // 구조: kakao_account -> profile -> nickname
            Map bodyMap = userRes.getBody();
            Map kakaoAccount = (Map) bodyMap.get("kakao_account");

            // kakoAccount가 null이 아니면, kakaoAccount에서 profile 키의 값을 꺼낸다.
            // 만약 null이면 null이다.
            // 키의 값은 Map으로 캐스팅하면서 profile이란 이름을 붙여준다.
            Map profile = kakaoAccount != null ? (Map) kakaoAccount.get("profile") : null;
            // 까넨 profil에 nickname이 null이 아니면 nickname의 키의 값을 꺼낸다.
            // 그리고 String 타입인 nickname 변수에 넣는다.
            String nickname = profile != null ? (String) profile.get("nickname") : null;

            // 실제 서비스라면 세션/JWT/회원 매칭이 필요하지만,
            // 이 프로젝트의 목표는 카카오 API로 닉네임만 받아오는 연습이므로 nickname만 반환한다.
            Map<String, Object> result = new HashMap<>();
            String safeNickname = (nickname != null) ? nickname : "KakaoUser";
            result.put("nickname", safeNickname);
            return ResponseEntity.ok(result);

            // Exception은 광범위한 예외로, 대부분의 예외를 다 잡아버리는 광범위한 캐치이다.
            // 단 Error는 잡지 않음. 가급적이면 구체적인 예외를 잡는게 더 좋은데, 이건 그냥 연습용 프로젝트이니...
        } catch (Exception ex) {
            // 예외의 클래스명, 메시지, 호출 경로를 콘솔로 출력한다.
            // 좋은 처리 방법은 아니다. 콘솔만 찍어서 로그 수집이나 검색도 어렵고, 개인정보 노출 위험도 있음.
            // 카카오 디벨로퍼 들어가서 보면 알겠지만, 오류 처리 엄청 많음...
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("카카오 연동 중 오류");
        }
    }
}

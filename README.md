# 🔒 회원가입과 다양한 로그인 🔑

<br/>

<p align="center">

  <br/>
  최근에는 많은 웹사이트가 일반 로그인뿐만 아니라 소셜 로그인도 함께 제공하고 있습니다. <br/>
  평소 소셜 로그인을 자주 사용하면서, 사용자는 간단하게 로그인하지만 내부에서는 어떤 과정으로 인증이 처리되는지 궁금했습니다.<br/>
  그래서 일반 로그인과 소셜 로그인을 직접 구현하며, 그 차이를 이해하고자 프로젝트를 진행하게 되었습니다. <br/>
  <br/>

  <br/>

  <img width="800" height="450" alt="image" src="https://github.com/user-attachments/assets/474de505-7532-4cda-ad48-4c61f612c4c4" />
  
</p>

<br/>
<br/>
<br/>

### 🔶 프로젝트 관련 링크

+ [Blog (프로젝트 기록)](https://post-this.tistory.com/category/%F0%9F%92%BB%20%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8/%F0%9F%90%A0%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85%20%ED%8E%98%EC%9D%B4%EC%A7%80%F0%9F%90%A0)
+ Youtube (동작화면)
+ [Figma (다이어그램)](https://www.figma.com/board/pcWxgbFCWQUnnIW3W1hrZi/%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8?node-id=0-1&t=NPUJ2hrnFEb7meeQ-1)


<br/>
<br/>


### 🔶 프로젝트 설명

<br/>

<p align="center">

  <img width="800" height="500" alt="스크린샷 2026-07-03 오후 8 56 27" src="https://github.com/user-attachments/assets/89561780-116a-4091-9e56-ec9c44c8152f" />

</p>

<br/>

+ 사용자가 입력한 정보로 일반 회원가입을 진행합니다. 
+ 아이디와 이메일 중복 확인을 통해 이미 등록된 회원인지 검증합니다.
+ H2 데이터베이스로 회원 정보를 저장합니다.
+ BCryptPasswordEncoder를 사용해 비밀번호를 암호화하여 저장합니다.
+ 카카오와 네이버 OAuth API를 연동해 소셜 로그인 흐름을 구현했습니다.

<br/><br/> 

### 🔶 기술 스택 & 라이브러리
+ 백엔드 : Java 17, Spring Boot
+ 프론트엔드 : JavaScript, Vue
+ 데이터베이스 : H2 Database
+ 보안 : Spring Security, BCryptPasswordEncoder
+ 외부 API 연동 : Kakao OAuth API, Naver OAuth API

<br/><br/>

### 🔶 프로젝트 목표
+ Vue와 Spring을 연결해 웹사이트 만들기
+ 백엔드에서 CORS 문제 해결해보기
+ 입력값 검증, 비밀번호 암호화 구현하기
+ 카카오·네이버 소셜 로그인을 구현하며 인가 코드와 토큰 흐름 이해하기

<br/><br/>

### 🔶 핵심 로직
1) 비밀번호 암호화 <br/>
사용자가 입력한 회원 정보를 서버에서 받아 DB에 저장했습니다.

+ 이때 비밀번호는 평문으로 저장하지 않고 BCryptPasswordEncoder로 암호화한 뒤 저장했습니다.

```java
public Member save(Member member) {
    member.setPw(passwordEncoder.encode(member.getPw()));
    return memberRepository.save(member);
}
```

<br/><br/>

----

2) 일반 로그인 검증 <br/>
일반 로그인은 사용자가 입력한 아이디로 회원을 조회합니다.

+ 입력한 비밀번호와 DB에 저장된 암호화 비밀번호를 비교하여 로그인 성공 여부를 판단했습니다.

```java
public boolean validateMember(LoginRequest loginRequest) {
    return memberRepository.findByUserId(loginRequest.getUserId())
            .map(member -> passwordEncoder.matches(
                    loginRequest.getPw(),
                    member.getPw()
            ))
            .orElse(false);
}
```

<br/><br/>

----

3) 카카오/네이버 소셜 로그인 <br/>
카카오와 네이버 OAuth 로그인을 구현했습니다.

+ 프론트엔드에서 전달받은 인가 코드를 이용해 access token을 요청했습니다.
+ 발급받은 토큰으로 사용자 정보를 조회해 로그인 화면에 표시했습니다.

```java
@PostMapping("/oauth/kakao")
public ResponseEntity<Map<String, String>> kakaoLogin(@RequestBody Map<String, String> body) {
    String code = body.get("code");

    String accessToken = kakaoService.getAccessToken(code);
    String nickname = kakaoService.getUserInfo(accessToken);

    return ResponseEntity.ok(Map.of("nickname", nickname));
}
```

<br/><br/><br/>


### 🔶 문제 해결

### [ CORS 문제 ] <br/>

1) 문제 발생 <br/>
+ Vue에서 Spring Boot로 회원가입 요청을 보냈지만 정상적으로 처리되지 않은 문제가 발생했습니다. <br/>
+ 프론트엔드는 localhost:5173, 백엔드는 localhost:8080에서 실행되고 있었기에 브라우저에서 CORS 오류가 발생했습니다.

<br/><br/>

2) 원인 파악 <br/>
+ 프론트엔드와 백엔드의 포트가 달라 브라우저가 서로 다른 출처로 판단했습니다.
+ 또한 Spring Secruity를 사용하고 있었기 때문에, CORS 요청이 Security Filter에서 먼저 차단되었습니다.

<br/><br/>

3) 문제 해결 <br/>
+ 백엔드 컨트롤러에서 Vue 개발 서버 주소를 허용했습니다.
+ Spring Security 설정에서도 CORS를 활성화했습니다.
  
```java
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class LoginController {
    // 로그인 API
}
```
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> {})
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()
        );

    return http.build();
}
```

<br/><br/>


### 🔶 아쉬운 점 및 개선 방향
1) 로그아웃 방식 <br/>

+ 이 프로젝트는 일반 로그인과 소셜 로그인의 동작 흐름을 이해하는 데 초점을 둔 프로젝트였습니다.
+ 로그인 이후의 인증 상태 관리와 로그아웃 처리는 깊게 구현하지 못했습니다.
+ 로그아웃은 localStorage 값을 삭제해 화면 상태를 초기화하는 방식에 머물렀고
+ 실제 서비스처럼 세션 만료나 토큰 폐기까지 다루지는 못했습니다.
+ 이 경험을 바탕으로 다음 프로젝트에서는 Keycloak을 도입해 로그인, 로그아웃, 토큰 기반 이증 흐름을 더 명확하게 구현했습니다.


<br/><br/>






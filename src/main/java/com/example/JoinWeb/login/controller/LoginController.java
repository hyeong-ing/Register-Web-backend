package com.example.JoinWeb.login.controller;

import com.example.JoinWeb.register.service.MemberService;
import com.example.JoinWeb.login.check.CheckLogin;
import com.example.JoinWeb.login.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class LoginController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {

        String idCheckResult = CheckLogin.checkId(loginRequest.getUserId());
        if (!"ok".equals(idCheckResult)) {
            return ResponseEntity.badRequest().body(idCheckResult);
        }

        String pwCheckResult = CheckLogin.checkPw(loginRequest.getPw());
        if (!"ok".equals(pwCheckResult)) {
            return ResponseEntity.badRequest().body(pwCheckResult);
        }

        boolean validated = memberService.validateMember(loginRequest);
        if (!validated) {
            return ResponseEntity.badRequest().body("아이디와 비밀번호를 다시 확인해주세요.");
        }

        return ResponseEntity.ok("ok");
    }
}

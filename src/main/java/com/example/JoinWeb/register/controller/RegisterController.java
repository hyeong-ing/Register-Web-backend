package com.example.JoinWeb.register.controller;

import com.example.JoinWeb.register.dto.RegisterRequest;
import com.example.JoinWeb.register.Member;
import com.example.JoinWeb.register.service.MemberService;
import com.example.JoinWeb.register.check.CheckName;
import com.example.JoinWeb.register.check.CheckPw;
import com.example.JoinWeb.register.check.CheckTel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class RegisterController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

        // 이름
        String nameCheckResult = CheckName.checkName(request.getName());
        if (!"ok".equals(nameCheckResult)) {
            return ResponseEntity.badRequest().body(nameCheckResult);
        }


        // 패스워드
        String pwCheckResult = CheckPw.checkPw(request.getPw(), request.getPwConfirm());
        if (!"ok".equals(pwCheckResult)) {
            return ResponseEntity.badRequest().body(pwCheckResult);
        }

        // 폰넘버
        String telCheckResult = CheckTel.checkTel(request.getTel());
        if (!"ok".equals(telCheckResult)) {
            return ResponseEntity.badRequest().body(telCheckResult);
        }


        Member member = new Member();
        member.setName(request.getName());
        member.setUserId(request.getUserId());
        member.setPw(request.getPw());
        member.setTel(request.getTel());
        member.setBirth(request.getBirth());
        member.setEmail(request.getEmail());

        memberService.save(member);


        return ResponseEntity.ok("ok");
    }
}

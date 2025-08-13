package com.example.JoinWeb.register.controller;

import com.example.JoinWeb.dto.EmailDuplicate;
import com.example.JoinWeb.dto.IdDuplicate;
import com.example.JoinWeb.register.Member;
import com.example.JoinWeb.register.MemberService;
import com.example.JoinWeb.register.check.CheckEmail;
import com.example.JoinWeb.register.check.CheckId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class DuplicateController {

    // 처음엔 바로 받아서 하려고 했는데 그런게 안되는듯... 그래서 객체를 따로 받는 DTO 설정해줌
    @Autowired
    private MemberService memberService;

    @PostMapping("/idDuplicate")
    public ResponseEntity<String> idDuplicate(@RequestBody IdDuplicate request) {

        String userId = request.getUserId();
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body("id를 입력해주세요.");
        }

        String checkResult = CheckId.checkId(userId);
        if (!"ok".equals(checkResult)) {
            return ResponseEntity.badRequest().body(checkResult);
        }

        // 궁금한거.
        // 왜 request.get뒤엔 대문자임?
        boolean existUserId = memberService.existByUserId(request.getUserId());
        if (existUserId == true) {
            return ResponseEntity.badRequest().body("id가 중복되었습니다.");
        } else {
            return ResponseEntity.ok("ok");
        }
    }

    @PostMapping("/emailDuplicate")
    public ResponseEntity<String> emailDuplicate(@RequestBody EmailDuplicate request) {

        String email = request.getEmail();
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("email을 입력해주세요.");
        }

        String checkResult = CheckEmail.checkEmail(email);
        if (!"ok".equals(checkResult)) {
            return ResponseEntity.badRequest().body(checkResult);
        }

        boolean existEmail = memberService.existByEmail(request.getEmail());
        if (existEmail == true) {
            return ResponseEntity.badRequest().body("email이 중복되었습니다.");
        } else {
            return ResponseEntity.ok("ok");
        }
    }

}

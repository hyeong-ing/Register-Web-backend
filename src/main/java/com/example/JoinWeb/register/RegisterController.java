package com.example.JoinWeb.register;

import com.example.JoinWeb.dto.RegisterRequest;
import com.example.JoinWeb.register.check.CheckEmail;
import com.example.JoinWeb.register.check.CheckName;
import com.example.JoinWeb.register.check.CheckPwd;
import com.example.JoinWeb.register.check.CheckTel;
import jakarta.validation.Valid;
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
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {

        // 이름
        String nameCheckResult = CheckName.checkName(request.getName());
        if (!"ok".equals(nameCheckResult)) {
            return ResponseEntity.badRequest().body(nameCheckResult);
        }

        // 아이디

        // 패스워드
        String pwdCheckResult = CheckPwd.checkPwd(request.getPwd(), request.getPwdConfirm());
        if (!"ok".equals(pwdCheckResult)) {
            return ResponseEntity.badRequest().body(pwdCheckResult);
        }

        // 폰넘버
        String telCheckResult = CheckTel.checkTel(request.getTel());
        if (!"ok".equals(telCheckResult)) {
            return ResponseEntity.badRequest().body(telCheckResult);
        }

        // 이메일
        String emailCheckResult = CheckEmail.checkEmail(request.getEmail());
        if (!"ok".equals(emailCheckResult)) {
            return ResponseEntity.badRequest().body(emailCheckResult);
        }

        System.out.println("=========================");
        System.out.println("회원가입 정보가 들어왔습니다.");
        System.out.println("=========================");
        System.out.println(request);
        System.out.println("=========================");

        Member member = new Member();
        member.setName(request.getName());
        member.setUserId(request.getUserId());
        member.setPwd(request.getPwd());
        member.setTel(request.getTel());
        member.setBirth(request.getBirth());
        member.setEmail(request.getEmail());

        memberService.save(member);
        return ResponseEntity.ok("ok");
    }
}

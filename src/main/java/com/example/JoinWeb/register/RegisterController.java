package com.example.JoinWeb.register;

import com.example.JoinWeb.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class RegisterController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        System.out.println("==== /register 요청이 들어왔습니다! ====");
        System.out.println(request);

        if (!request.getPwd().equals(request.getPwdConfirm())) {
            return "비밀번호가 일치하지 않습니다.";
        }

        Member member = new Member();
        member.setName(request.getName());
        member.setUserId(request.getUserId());
        member.setPwd(request.getPwd());
        member.setTel(request.getTel());
        member.setBirth(request.getBirth());
        member.setEmail(request.getEmail());

        memberService.save(member);
        return "ok";
    }
}

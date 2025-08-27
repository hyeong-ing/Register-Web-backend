package com.example.JoinWeb.register.service;

import com.example.JoinWeb.register.Member;
import com.example.JoinWeb.login.dto.LoginRequest;
import com.example.JoinWeb.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Boolean existByUserId (String userId) {
        return memberRepository.existsByUserId(userId);
    }

    public Boolean existByEmail (String email) {
        return memberRepository.existsByEmail(email);
    }

    public Member save(Member member) {
        member.setPwd(passwordEncoder.encode(member.getPwd()));
        return memberRepository.save(member);
    }

    public boolean validateMember(LoginRequest loginRequest) {
        return memberRepository.findByUserId(loginRequest.getUserId())
                .map(member -> passwordEncoder.matches(loginRequest.getPwd(), member.getPwd()))
                .orElse(false);
    }

}

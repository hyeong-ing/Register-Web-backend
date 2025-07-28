package com.example.JoinWeb.register;

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

    public Member save(Member member) {
        member.setPwd(passwordEncoder.encode(member.getPwd()));
        return memberRepository.save(member);
    }
}

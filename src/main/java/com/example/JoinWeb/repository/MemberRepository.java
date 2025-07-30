package com.example.JoinWeb.repository;

import com.example.JoinWeb.register.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {
    // by 안붙였다고 오류남
    boolean existsByUserId(String userId);
    boolean existsByEmail (String email);
}

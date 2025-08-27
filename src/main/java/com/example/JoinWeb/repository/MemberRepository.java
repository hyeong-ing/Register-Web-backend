package com.example.JoinWeb.repository;

import com.example.JoinWeb.register.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByUserId(String userId);
    boolean existsByEmail (String email);

    Optional<Member> findByUserId(String userId);
}

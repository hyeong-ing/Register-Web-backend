package com.example.JoinWeb.repository;

import com.example.JoinWeb.register.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {

}

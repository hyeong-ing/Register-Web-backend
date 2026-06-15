package com.example.JoinWeb.register;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

// 데이터베이스에서 저장/조회하는 클래스가 된다.
@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(nullable = false, unique = true)
    private String userId;
    private String pw;
    private String tel;
    private String birth;
    @Column(nullable = false, unique = true)
    private String email;
}

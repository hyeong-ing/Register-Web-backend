package com.example.JoinWeb.register.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 프론트엔드와 연결하는 클래스가 될 것임.
@ToString
@Getter
@Setter
public class RegisterRequest {
    private String name;
    private String userId;
    private String pwd;
    private String pwdConfirm;
    private String tel;
    private String birth;
    private String email;
}

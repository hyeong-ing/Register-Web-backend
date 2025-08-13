package com.example.JoinWeb.register.check;

import com.example.JoinWeb.dto.RegisterRequest;

import java.util.regex.Pattern;

// 1. null 체크
// 2. 이메일 패턴 체크
// 3. 중복 확인
public class CheckEmail {

    private static final String EMAIL_PATTERN =
            "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public static String checkEmail(String email) {
        //Blank와 null의 차이 알아보기
        if(email == null || email.isBlank()) {
            return "이메일을 입력해주세요.";
        }
        if (!pattern.matcher(email).matches()) {
            return "이메일 양식을 확인해주세요.";
        }

        return "ok";
    }
}

package com.example.JoinWeb.register.check;

import com.example.JoinWeb.dto.RegisterRequest;

import java.util.regex.Pattern;


public class CheckTel {

    private static final String TEL_PATTERN = "^01(?:0|1|[6-9])[-](\\d{3}|\\d{4})[-](\\d{4})$";

    private static final Pattern pattern = Pattern.compile(TEL_PATTERN);

    public static String checkTel(String tel) {

        if(tel == null || tel.isBlank()) {
            return "휴대폰 번호를 입력해주세요.";
        }
        if (!pattern.matcher(tel).matches()) {
            return "휴대폰 번호 양식은 010-0000-0000 입니다.";
        }

        return "ok";
    }
}

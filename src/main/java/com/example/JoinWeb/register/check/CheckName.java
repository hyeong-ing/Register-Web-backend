package com.example.JoinWeb.register.check;

import com.example.JoinWeb.dto.RegisterRequest;

import java.util.regex.Pattern;

// 1. null인지 체크
// 2. 한글 이름인지 체크
public class CheckName {

    RegisterRequest registerRequest;

    private static final String NAME_PATTERN = "^[가-힣]{2,5}$";

    private static final Pattern pattern = Pattern.compile(NAME_PATTERN);

    public static String checkName(String name) {
        //Blank와 null의 차이 알아보기
        if(name == null || name.isEmpty()) {
            return "성함을 입력해주세요.";
        }
        if (!pattern.matcher(name).matches()) {
            return "성함은 한글로 작성하셔야합니다.";
        }

        return "ok";
    }

}

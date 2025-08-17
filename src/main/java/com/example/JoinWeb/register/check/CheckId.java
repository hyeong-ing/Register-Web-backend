package com.example.JoinWeb.register.check;

import java.util.regex.Pattern;

public class CheckId {

    private static final String ID_PATTERN = "^(?=.*\\d)[A-Za-z][A-Za-z0-9]{3,19}$";

    private static final Pattern pattern = Pattern.compile(ID_PATTERN);

    public static String checkId(String userId) {

        if(userId == null || userId.isBlank()) {
            return "아이디를 입력해주세요.";
        }

        if (!pattern.matcher(userId).matches()) {
            return "아이디의 첫글자는 영어이며, 영어와 숫자를 섞어 4자리 이상 20자리 이하만 가능합니다.";
        }

        return "ok";
    }
}

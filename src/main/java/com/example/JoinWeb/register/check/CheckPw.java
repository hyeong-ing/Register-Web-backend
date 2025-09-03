package com.example.JoinWeb.register.check;


import java.util.regex.Pattern;

public class CheckPw {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*()-+=]).{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static String checkPw(String pw, String pwConfirm) {
        if(pw == null || pw.isBlank()) {
            return "패스워드를 입력해주세요.";
        }
        if (pwConfirm == null || pwConfirm.isBlank()) {
            return "패스워드 확인란을 입력해주세요.";
        }
        if (!pw.equals(pwConfirm)) {
            return "패스워드가 일치하지 않습니다.";
        }
        if (!pattern.matcher(pw).matches()) {
            return "패스워드에 대문자와 특수문자가 포함된 8글자 이상이어야합니다.";
        }

        return "ok";
    }


}

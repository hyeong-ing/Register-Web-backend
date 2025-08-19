package com.example.JoinWeb.login.check;

public class CheckLogin {

    public static String checkId(String userId) {
        if(userId == null || userId.isBlank()) {
            return "아이디를 입력해주세요.";
        }
        return "ok";
    }

    public static String checkPwd(String pwd) {
        if(pwd == null || pwd.isBlank()) {
            return "비밀번호를 입력해주세요.";
        }
        return "ok";
    }
}

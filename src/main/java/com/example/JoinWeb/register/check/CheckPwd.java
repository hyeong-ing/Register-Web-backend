package com.example.JoinWeb.register.check;


import com.example.JoinWeb.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.regex.Pattern;

// 1. 패턴 체크을 체크하고 틀렸으면 오류 메시지 먼저 출력
// 2. 패턴 체크 괜찮으면 confirme이랑 다른 경우 오류 메시지 출력
public class CheckPwd {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*()-+=]).{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    // 오류 메세지를 string으로 보내고 -> 컨트롤러에서 data에 담아 alert하자.
    public static String checkPwd(String pwd, String pwdConfirm) {
        if(pwd == null || pwd.isEmpty()) {
            return "패스워드를 입력해주세요.";
        }
        if (!pwd.equals(pwdConfirm)) {
            return "패스워드가 일치하지 않습니다.";
        }
        if (!pattern.matcher(pwd).matches()) {
            return "패스워드에 대문자와 특수문자가 포함된 8글자 이상이어야합니다.";
        }

        return "ok";
    }


}

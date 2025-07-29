package com.example.JoinWeb.dto;

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

    // null이 들어오면 오류 발생
    // 컨트롤러의 @Valid 까지
    //@NotBlank
    //@Pattern(regexp = "^[가-힣]{2,5}$", message = "이름은 2-5글자 한글만 가능합니다.")
    private String name;
    @NotBlank
    private String userId;
    private String pwd;
    private String pwdConfirm;
    //@NotBlank
    //@Pattern(regexp = "^01(?:0|1|[6-9])[-]?(\\d{3}|\\d{4})[-]?(\\d{4})$", message = "핸드폰 번호 양식에 맞지 않습니다.")
    private String tel;
    private String birth;
    // @NotBlank
    // @Pattern(regexp="^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$", message="이메일 주소 양식을 확인해주세요")
    private String email;

}

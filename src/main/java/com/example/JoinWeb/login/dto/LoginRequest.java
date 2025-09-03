package com.example.JoinWeb.login.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// @Data도 쓸 수 있지만, 보안상 쓰지 않겠음.
// @exclude="pwd"와 @JsonProperty에 대해 블로그 적기.
@Getter
@Setter
@ToString(exclude="pw")
public class LoginRequest {
    private String userId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String pw;
}

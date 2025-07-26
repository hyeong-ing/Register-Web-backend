package com.example.JoinWeb;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Main {

    @GetMapping("/main")
    public String mainView() {
        return "메인화면입니다.";
    }

}

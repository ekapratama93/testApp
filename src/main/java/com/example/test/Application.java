package com.example.test;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Application {
    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test(){
        return "test";
    }

    @GetMapping(value = "/ping", produces = MediaType.TEXT_PLAIN_VALUE)
    public String pong(){
        return "pong";
    }
}

package io.elice.shoppingmall.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


    @Controller
    public class MainPageController {

        @GetMapping("/")
        public String getMainPage() {
            return "/home/home.html";
        }
    }




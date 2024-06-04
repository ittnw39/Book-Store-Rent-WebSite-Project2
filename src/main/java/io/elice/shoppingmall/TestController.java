package io.elice.shoppingmall;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/asdf")
    public String getPage() {
        return "login/login.html";
    }
}
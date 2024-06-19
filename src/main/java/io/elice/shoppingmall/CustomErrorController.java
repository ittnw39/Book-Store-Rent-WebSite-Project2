package io.elice.shoppingmall;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


public class CustomErrorController implements ErrorController {
    @GetMapping("/error")
    public String handleError() {
        return "page-not-found";
    }
}

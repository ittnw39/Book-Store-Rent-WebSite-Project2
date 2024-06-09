package io.elice.shoppingmall.user.controller;

import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimeController {
    @GetMapping("/api/time")
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }
}

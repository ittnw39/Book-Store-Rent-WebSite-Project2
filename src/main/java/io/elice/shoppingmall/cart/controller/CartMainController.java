package io.elice.shoppingmall.cart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartMainController {

    @GetMapping("/cart")
    public String cartGetPage() {
        return "/cart/cart.html";
    }

}

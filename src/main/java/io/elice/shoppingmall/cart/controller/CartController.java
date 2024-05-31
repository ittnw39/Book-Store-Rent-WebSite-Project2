package java.io.elice.shoppingmall.cart.controller;

import io.elice.shoppingmall.cart.dto.CartItemDto;
import io.elice.shoppingmall.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

public class CartController {
    private final CartService cartService;

    @PostMapping(value = "/cart")
    public @ResponseBody
    ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult, Principal principal) {
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FiledError>  fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>
                    (sb.toString(), HttpStatus.BAD_REQUEST);

        }
    }
}

package io.elice.shoppingmall.product.controller;

import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductViewController {

    @GetMapping("/books")
    public String viewBookListPage() {
        return "/product-list/product-list.html";
    }

    @GetMapping("/book/{id}")
    public String viewBookPage() {
        return "/product-detail/product-detail.html";
    }

    //관리자 페이지
    @GetMapping("/admin/books")
    public String adminBookListPage() {
        return "/admin-product-list/admin-product-list.html";
    }

    @GetMapping("/admin/book")
    public String createBookPage() {
        return "/product-add/product-add.html";
    }
}

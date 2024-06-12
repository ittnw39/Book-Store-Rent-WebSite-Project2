package io.elice.shoppingmall.product.controller;

import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductViewController {

    //전체 목록 조회 페이지
    @GetMapping("/books")
    public String viewBookListPage() {
        return "/product-list/product-list.html";
    }

    //상세 제품 조회 페이지
    @GetMapping("/book/{id}")
    public String viewBookPage() {
        return "/product-detail/product-detail.html";
    }

    //전체 목록 조회 페이지(관리자용)
    @GetMapping("/admin/books")
    public String adminBookListPage() {
        return "/admin-product-list/admin-product-list.html";
    }

    //제품 등록 페이지(관리자용)
    @GetMapping("/admin/book")
    public String createBookPage() {
        return "/product-add/product-add.html";
    }
}

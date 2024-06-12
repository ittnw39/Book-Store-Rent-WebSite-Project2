package io.elice.shoppingmall.product.controller;

import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

    //카테고리별 책 목록
    @GetMapping("/books/category")
    public String viewBookListByCategoryPage(@RequestParam Long categoryId, Model model) {
        model.addAttribute("categoryId", categoryId);
        return "/product-list/product-list.html";
    }

    //keyword 포함된 title, author.name 책 목록
    @GetMapping("/books/search")
    public String viewBookListBySearchPage(@RequestParam String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        return "/product-list/product-list.html";
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

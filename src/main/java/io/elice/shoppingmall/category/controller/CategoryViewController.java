package io.elice.shoppingmall.category.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CategoryViewController {

    @GetMapping("/admin/category")
    public String getCategoryPage() {
        return "/admin-categories/admin-categories.html";
    }
}

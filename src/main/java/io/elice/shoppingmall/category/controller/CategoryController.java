package io.elice.shoppingmall.category.controller;

import io.elice.shoppingmall.category.entity.Category;
import io.elice.shoppingmall.category.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/category")
    public String getAllCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin-categories";
    }

    @GetMapping("/category/add")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "category-add";
    }

    @PostMapping("/category")
    public String addCategory(@ModelAttribute Category category) {
        categoryService.addCategory(category);
        return "redirect:/admin/category";
    }

    // 단일 카테고리 삭제
    @DeleteMapping("/category/{categoryId}")
    public String deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return "redirect:/admin/category";
    }

    // 선택된 카테고리 삭제
    @DeleteMapping("/category")
    @ResponseBody // 클라이언트로부터 받은 JSON 배열을 List<Long> 타입으로 변환하기 위함
    public String deleteCategories(@RequestBody List<Long> categoryIds) {
        for (Long categoryId : categoryIds) {
            categoryService.deleteCategory(categoryId);
        }
        return "Categories deleted successfully";
    }
}

package io.elice.shoppingmall.category.controller;

import io.elice.shoppingmall.category.entity.Category;
import io.elice.shoppingmall.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/category")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping("/category")
    public Category addCategory(@Valid @RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    @PutMapping("/category/{categoryId}")
    public Category updateCategory(@PathVariable Long categoryId, @Valid @RequestBody Category categoryDetails) {
        return categoryService.updateCategory(categoryId, categoryDetails);
    }


    // 단일 카테고리 삭제
    @DeleteMapping("/category/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
    }

    // 선택된 카테고리 삭제
    @DeleteMapping("/category")
    public void deleteCategories(@RequestBody List<Long> categoryIds) {
        categoryService.deleteCategories(categoryIds);
    }
}

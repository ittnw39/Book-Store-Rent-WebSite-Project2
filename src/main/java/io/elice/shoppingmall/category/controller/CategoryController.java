package io.elice.shoppingmall.category.controller;

import io.elice.shoppingmall.category.dto.CategoryDto;
import io.elice.shoppingmall.category.service.CategoryService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String getCategoryPage() {
        return "admin-categories/admin-categories.html";
    }

    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto createdCategory = categoryService.addCategory(categoryDto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    @ResponseBody
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryDto categoryDetails) {
        CategoryDto updatedCategory = categoryService.updateCategory(categoryId, categoryDetails);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    // 단일 카테고리 삭제
    @DeleteMapping("/{categoryId}")
    @ResponseBody
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 선택된 카테고리 삭제
    @DeleteMapping
    @ResponseBody
    public ResponseEntity<Void> deleteCategories(@RequestBody List<Long> categoryIds) {
        categoryService.deleteCategories(categoryIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}


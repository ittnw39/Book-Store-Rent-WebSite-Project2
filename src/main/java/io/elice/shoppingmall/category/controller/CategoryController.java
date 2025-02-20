package io.elice.shoppingmall.category.controller;
import io.elice.shoppingmall.category.dto.CategoryDto;
import io.elice.shoppingmall.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 일반 API
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // 관리자 API
    @GetMapping("/admin/categories")
    public ResponseEntity<List<CategoryDto>> getAllAdminCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PostMapping("/admin/category")
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto createdCategory = categoryService.addCategory(categoryDto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @PutMapping("/admin/category/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryDto categoryDetails) {
        CategoryDto updatedCategory = categoryService.updateCategory(categoryId, categoryDetails);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    // 단일 카테고리 삭제
    @DeleteMapping("/admin/category/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 선택된 카테고리 삭제
    @DeleteMapping("/admin/category")
    public ResponseEntity<Void> deleteCategories(@RequestBody List<Long> categoryIds) {
        categoryService.deleteCategories(categoryIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

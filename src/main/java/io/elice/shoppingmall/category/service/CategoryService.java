package io.elice.shoppingmall.category.service;

import io.elice.shoppingmall.category.dto.CategoryDto;
import io.elice.shoppingmall.category.repository.CategoryRepository;
import io.elice.shoppingmall.category.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;


    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id " + categoryId));

        return convertToDto(category);
    }

    public CategoryDto addCategory(CategoryDto categoryDto) {
        if(categoryDto.getName() == null || categoryDto.getName().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        Category category = new Category();
        category.setName(categoryDto.getName());
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDetails) {
        if(categoryDetails.getName() == null || categoryDetails.getName().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id " + categoryId));
        category.setName(categoryDetails.getName());
        Category updatedCategory = categoryRepository.save(category);
        return convertToDto(updatedCategory);
    }

    public void deleteCategory(Long categoryId) {
        if(!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category not found with id " + categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }

    public void deleteCategories(List<Long> categoryIds) {
        for(Long categoryId : categoryIds) {
            if(!categoryRepository.existsById(categoryId)) {
                throw new IllegalArgumentException("Category not found with id " + categoryId);
            }
            deleteCategory(categoryId);
        }
    }

    private CategoryDto convertToDto(Category category) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getCreatedAt() != null ? formatter.format(category.getCreatedAt()) : null,
                category.getUpdatedAt() != null ? formatter.format(category.getUpdatedAt()) : null
        );
    }
}


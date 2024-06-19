package io.elice.shoppingmall.category.service;

import io.elice.shoppingmall.category.dto.CategoryDto;
import io.elice.shoppingmall.category.repository.CategoryRepository;
import io.elice.shoppingmall.category.entity.Category;
import java.util.Optional;
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

    public Optional<Category> searchCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    public CategoryDto addCategory(CategoryDto categoryDto) {
        if(categoryDto.getName() == null || categoryDto.getName().isEmpty()) {
            throw new IllegalArgumentException("카테고리 이름은 공백일 수 없습니다.");
        }

        Optional<Category> existingCategory = categoryRepository.findByName(categoryDto.getName());
        if(existingCategory.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다.");
        }

        Category category = new Category();
        category.setName(categoryDto.getName());
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDetails) {
        if(categoryDetails.getName() == null || categoryDetails.getName().isEmpty()) {
            throw new IllegalArgumentException("카테고리 이름은 공백일 수 없습니다.");
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


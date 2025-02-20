 package io.elice.shoppingmall.category.service;

 import io.elice.shoppingmall.category.dto.CategoryDto;
 import io.elice.shoppingmall.category.entity.Category;
 import io.elice.shoppingmall.category.repository.CategoryRepository;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.MockitoAnnotations;

 import java.util.Arrays;
 import java.util.List;
 import java.util.Optional;

 import static org.junit.jupiter.api.Assertions.assertEquals;
 import static org.junit.jupiter.api.Assertions.assertNotNull;
 import static org.mockito.ArgumentMatchers.any;
 import static org.mockito.Mockito.*;

 class CategoryJUnitTest {

     @Mock
     private CategoryRepository categoryRepository;

     @InjectMocks
     private CategoryService categoryService;

     @BeforeEach
     void setUp() {
         MockitoAnnotations.openMocks(this);
     }

     @DisplayName("카테고리 전체 조회")
     @Test
     void testGetAllCategories() {

         Category category1 = new Category();
         category1.setId(1L);
         category1.setName("Category 1");

         Category category2 = new Category();
         category2.setId(2L);
         category2.setName("Category 2");

         when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

         List<CategoryDto> categories = categoryService.getAllCategories();

         assertNotNull(categories);
         assertEquals(2, categories.size());
         assertEquals("Category 1", categories.get(0).getName());
         assertEquals("Category 2", categories.get(1).getName());
     }

     @DisplayName("카테고리 조회")
     @Test
     void testGetCategoryById() {
         Category category = new Category();
         category.setId(1L);
         category.setName("Category 1");

         when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

         CategoryDto categoryDto = categoryService.getCategoryById(1L);

         assertNotNull(categoryDto);
         assertEquals("Category 1", categoryDto.getName());
     }

     @DisplayName("카테고리 추가 테스트")
     @Test
     void testAddCategory() {
         CategoryDto categoryDto = new CategoryDto(null, "Category 1", null, null);
         Category category = new Category();
         category.setName("Category 1");

         when(categoryRepository.save(any(Category.class))).thenReturn(category);

         CategoryDto createdCategory = categoryService.addCategory(categoryDto);

         assertNotNull(createdCategory);
         assertEquals("Category 1", createdCategory.getName());
     }

     @DisplayName("카테고리 수정 테스트")
     @Test
     void testUpdateCategory() {
         Category existingCategory = new Category();
         existingCategory.setId(1L);
         existingCategory.setName("Category 1");

         Category updatedCategory = new Category();
         updatedCategory.setId(1L);
         updatedCategory.setName("Updated Category");

         CategoryDto updatedCategoryDto = new CategoryDto(1L, "Updated Category", null, null);

         when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
         when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

         CategoryDto result = categoryService.updateCategory(1L, updatedCategoryDto);

         assertNotNull(result);
         assertEquals("Updated Category", result.getName());
     }

     @DisplayName("카테고리 단일 삭제 테스트")
     @Test
     void testDeleteCategory() {
         Long categoryId = 1L;

         when(categoryRepository.existsById(categoryId)).thenReturn(true);

         categoryService.deleteCategory(categoryId);

         verify(categoryRepository, times(1)).deleteById(categoryId);
     }

     @DisplayName("카테고리 선택 삭제 테스트")
     @Test
     void testDeleteCategories() {
         List<Long> categoryIds = Arrays.asList(1L, 2L);

         when(categoryRepository.existsById(1L)).thenReturn(true);
         when(categoryRepository.existsById(2L)).thenReturn(true);

         categoryService.deleteCategories(categoryIds);

         verify(categoryRepository, times(1)).deleteById(1L);
         verify(categoryRepository, times(1)).deleteById(2L);
     }

 }

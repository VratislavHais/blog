package com.vhais.blog.service;

import com.vhais.blog.model.Category;
import com.vhais.blog.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceUnitTest {
    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CategoryServiceImpl service;

    @Test
    public void testFindCategoryByName_whenCategoryExists() {
        String categoryName = "test";
        Category category = new Category();
        category.setName(categoryName);
        when(repository.findByName(categoryName)).thenReturn(Optional.of(category));

        Optional<Category> foundCategory = service.getCategoryByName(categoryName);
        assertThat(foundCategory).isNotEmpty();
        assertThat(foundCategory).contains(category);
    }

    @Test
    public void testFindCategoryByName_whenCategoryDoesNotExist() {
        Optional<Category> foundCategory = service.getCategoryByName("test");
        assertThat(foundCategory).isEmpty();
    }

    @Test
    public void testSaveCategory_andRetrieve() {
        String categoryName = "test";
        Category category = new Category();
        category.setName(categoryName);

        when(repository.save(category)).thenReturn(category);
        when(repository.findByName(categoryName)).thenReturn(Optional.of(category));

        Category saved = service.saveCategory(category);
        assertThat(saved).isEqualTo(category);

        Optional<Category> retrieved = service.getCategoryByName(categoryName);
        assertThat(retrieved).isNotEmpty();
        assertThat(retrieved).contains(category);
    }

    @Test
    public void testGetAllCategories_whenMultipleExist() {
        List<Category> categories = List.of(
                new Category("test1"),
                new Category("test2"),
                new Category("test3"));

        when(repository.findAll()).thenReturn(categories);

        List<Category> retrieved = service.getAllCategories();
        assertThat(retrieved).hasSameElementsAs(categories);
    }

    @Test
    public void testGetAllCategories_whenNoCategoriesExist() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<Category> retrieved = service.getAllCategories();

        assertThat(retrieved).isEmpty();
    }
}

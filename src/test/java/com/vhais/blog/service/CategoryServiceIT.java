package com.vhais.blog.service;

import com.vhais.blog.model.Category;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.User;
import com.vhais.blog.repository.CategoryRepository;
import com.vhais.blog.repository.PostRepository;
import com.vhais.blog.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Tag("integration")
public class CategoryServiceIT {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryServiceImpl service;

    @BeforeEach
    public void setup() {
        categoryRepository.deleteAll();
    }

    @Test
    public void testSaveCategory_andRetrieve() {
        String categoryName = "test";
        Category category = new Category();
        category.setName(categoryName);

        Category saved = categoryRepository.save(category);
        assertThat(saved.getName()).isEqualTo(category.getName());

        Optional<Category> foundCategory = service.getCategoryByName(categoryName);
        assertThat(foundCategory).isNotEmpty();
        assertThat(foundCategory).contains(saved);
    }

    @Test
    public void testFindCategoryByName_whenCategoryDoesNotExist() {
        Optional<Category> foundCategory = service.getCategoryByName("test");
        assertThat(foundCategory).isEmpty();
    }

    @Test
    public void testGetAllCategories_whenMultipleExist() {
        List<Category> categories = List.of(
                new Category("test1"),
                new Category("test2"),
                new Category("test3"));

        List<Category> saved = categoryRepository.saveAll(categories);
        assertThat(saved).hasSize(categories.size());
        assertThat(saved.stream().map(Category::getName).collect(Collectors.toList())).hasSameElementsAs(categories.stream().map(Category::getName).collect(Collectors.toList()));

        List<Category> retrieved = service.getAllCategories();
        assertThat(retrieved).hasSize(categories.size());
        assertThat(retrieved).hasSameElementsAs(saved);
    }

    @Test
    public void testGetAllCategories_whenNoCategoriesExist() {
        List<Category> retrieved = service.getAllCategories();

        assertThat(retrieved).isEmpty();
    }
}

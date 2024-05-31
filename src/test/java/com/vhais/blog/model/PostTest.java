package com.vhais.blog.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PostTest {
    @Test
    public void whenAllFieldAreNull_thenValidatonFails() {
        final Set<String> nonNullFields = Set.of("Title", "Content", "Author", "Category");
        Post post = new Post();

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        Set<ConstraintViolation<Post>> violations = validator.validate(post);
        assertThat(violations).isNotEmpty();
        assertThat(violations.size()).isEqualTo(4);
        assertThat(violations).allMatch(violation -> nonNullFields.contains(violation.getMessage().split(" ", 2)[0]));
    }
}

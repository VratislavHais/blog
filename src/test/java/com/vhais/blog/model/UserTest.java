package com.vhais.blog.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
    @Test
    public void whenAllFieldAreNull_thenValidatonFails() {
        final Set<String> nonNullFields = Set.of("Email", "Username", "Password");
        User user = new User();

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations.size()).isEqualTo(3);
        assertThat(violations).allMatch(violation -> nonNullFields.contains(violation.getMessage().split(" ", 2)[0]));
    }

    @Test
    public void whenEmailIsInvalid_thenValidationFails() {
        String violationMessage = "Invalid email address";
        User user = new User();
        user.setPassword("test");
        user.setEmail("incorrect");
        user.setUsername("test");

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo(violationMessage);
    }
}

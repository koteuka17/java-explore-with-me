package ru.practicum.entity.util.notblanknull;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotBlankNullConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankNull {
    String message() default "Если поле не null, то оно не должно быть пустым";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

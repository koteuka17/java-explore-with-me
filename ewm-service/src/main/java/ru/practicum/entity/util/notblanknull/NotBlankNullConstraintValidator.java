package ru.practicum.entity.util.notblanknull;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotBlankNullConstraintValidator implements ConstraintValidator<NotBlankNull, String> {
    @Override
    public boolean isValid(String text, ConstraintValidatorContext constraintValidatorContext) {
        if (text == null) {
            return true;
        } else {
            return !text.isBlank();
        }
    }
}

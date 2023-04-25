package ru.practicum.shareit.validatrors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidIdValidator implements ConstraintValidator<ValidIdConstraint,Long> {


    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        return value > 0;
    }
}

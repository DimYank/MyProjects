package net.thumbtack.forums.dto.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class ForumNameValidator implements ConstraintValidator<ForumName, String> {

    @Value("${max_name_length}")
    private int maxNameLength;

    @Override
    public void initialize(ForumName constraintAnnotation) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isEmpty(s)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Forum name is empty!").addConstraintViolation();
            return false;
        }
        if (s.length() > maxNameLength) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Forum name " + s + " is too long!").addConstraintViolation();
            return false;
        }
        if (!s.matches("^[a-zA-Zа-яА-Я]+$")) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Forum name " + s + " is inappropriate!").addConstraintViolation();
            return false;
        }
        return true;
    }
}

package net.thumbtack.forums.dto.validation;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class MessageTagsValidator implements ConstraintValidator<MessageTags, List<String>> {
    @Override
    public void initialize(MessageTags constraintAnnotation) {

    }

    @Override
    public boolean isValid(List<String> strings, ConstraintValidatorContext constraintValidatorContext) {
        if (strings != null) {
            for (String string : strings) {
                if (StringUtils.isEmpty(string)) {
                    return false;
                }
            }
        }
        return true;
    }
}

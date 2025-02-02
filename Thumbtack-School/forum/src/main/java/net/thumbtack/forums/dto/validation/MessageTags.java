package net.thumbtack.forums.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MessageTagsValidator.class)
public @interface MessageTags {
    String message() default "One of the tags is empty string or null!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package csd.roster.annotation;

import csd.roster.validator.DateTimesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// For annotations, refer to: https://stackoverflow.com/a/43787036/16331209
// For baeldung reference: https://www.baeldung.com/spring-mvc-custom-validator
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimesValidator.class)
public @interface ValidDateTimes {
    String fromDateTime();
    String toDateTime();

    // Custom message
    String message() default "FromDateTime must be before ToDateTime";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

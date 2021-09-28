package csd.roster.annotation;

import csd.roster.validator.DateTimesValidator;

import javax.validation.Constraint;
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
}

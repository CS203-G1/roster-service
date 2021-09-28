package csd.roster.validator;

import csd.roster.annotation.ValidDateTimes;
import csd.roster.model.RosterEmployee;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

// Custom Validator to validate whether fromDateTime is before toDateTime
// reference: https://www.baeldung.com/spring-mvc-custom-validator
public class DateTimesValidator implements ConstraintValidator<ValidDateTimes, RosterEmployee> {

    private String fromDateTimeFieldName;
    private String toDateTimeFieldName;

}

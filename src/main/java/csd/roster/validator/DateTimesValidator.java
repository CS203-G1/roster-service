package csd.roster.validator;

import csd.roster.annotation.ValidDateTimes;
import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

// Custom Validator to validate whether fromDateTime is before toDateTime
// reference: https://www.baeldung.com/spring-mvc-custom-validator
public class DateTimesValidator implements ConstraintValidator<ValidDateTimes, Roster> {

    private String fromDateTimeFieldName;
    private String toDateTimeFieldName;

    @Override
    public void initialize(ValidDateTimes constraintAnnotation) {
        this.fromDateTimeFieldName = constraintAnnotation.fromDateTime();
        this.toDateTimeFieldName = constraintAnnotation.toDateTime();
    }

    @Override
    public boolean isValid(Roster roster, ConstraintValidatorContext constraintValidatorContext) {
        // What I am doing here is to get the value of rosterEmployee.fromDateTime and convert it to a LocalDateTime
        // object
        // reference: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/BeanWrapperImpl.html
        final LocalDateTime fromDateTime = (LocalDateTime) new BeanWrapperImpl(roster)
                .getPropertyValue(fromDateTimeFieldName);
        // Same as fromDateTime
        final LocalDateTime toDateTime = (LocalDateTime) new BeanWrapperImpl(roster)
                .getPropertyValue(toDateTimeFieldName);

        // return whether fromDatetime is before toDateTime
        // reference: https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html#isBefore-java.time.chrono.ChronoLocalDateTime-
        return fromDateTime.isBefore(toDateTime);
    }

}

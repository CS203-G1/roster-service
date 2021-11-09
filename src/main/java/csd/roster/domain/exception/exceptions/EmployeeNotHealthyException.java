package csd.roster.domain.exception.exceptions;

import csd.roster.domain.model.Employee;

public class EmployeeNotHealthyException extends RuleViolatedException {
    public EmployeeNotHealthyException(Employee employee) {
        super(String.format("Employee %s is not healthy", employee.getId()));
    }
}

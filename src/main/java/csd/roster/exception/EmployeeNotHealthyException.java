package csd.roster.exception;

import csd.roster.model.Employee;
import org.dom4j.rule.Rule;

public class EmployeeNotHealthyException extends RuleViolatedException {
    public EmployeeNotHealthyException(Employee employee) {
        super(String.format("Employee %s is not healthy", employee.getId()));
    }
}

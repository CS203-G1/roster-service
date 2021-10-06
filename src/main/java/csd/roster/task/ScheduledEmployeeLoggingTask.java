package csd.roster.task;

import csd.roster.model.Employee;
import csd.roster.service.EmployeeLogService;
import csd.roster.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledEmployeeLoggingTask {
    private static final Logger log = LoggerFactory.getLogger(ScheduledEmployeeLoggingTask.class);
    private EmployeeService employeeService;
    private EmployeeLogService employeeLogService;

    @Scheduled
    public void logEmployeeDetails() {
        // Get all employees
        List<Employee> employees = employeeService.getAllEmployees();
        // Loop through all employees and log one by one asynchronously
    }
}

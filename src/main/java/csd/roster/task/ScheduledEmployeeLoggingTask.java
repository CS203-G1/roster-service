package csd.roster.task;

import csd.roster.service.EmployeeLogService;
import csd.roster.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledEmployeeLoggingTask {
    private static final Logger log = LoggerFactory.getLogger(ScheduledEmployeeLoggingTask.class);
    private EmployeeService employeeService;
    private EmployeeLogService employeeLogService;

    @Scheduled
    public void logEmployeeDetails() {
        // Get all employees
        // Loop through all employees and log one by one asynchronously
    }
}

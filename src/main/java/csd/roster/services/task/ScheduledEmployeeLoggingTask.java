package csd.roster.services.task;

import csd.roster.model.Employee;
import csd.roster.services.service.interfaces.EmployeeLogService;
import csd.roster.services.service.interfaces.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class ScheduledEmployeeLoggingTask {
    private static final Logger log = LoggerFactory.getLogger(ScheduledEmployeeLoggingTask.class);
    private final EmployeeService employeeService;
    private final EmployeeLogService employeeLogService;

    public ScheduledEmployeeLoggingTask(EmployeeService employeeService, EmployeeLogService employeeLogService) {
        this.employeeService = employeeService;
        this.employeeLogService = employeeLogService;
    }

    // cron = "<second> <minute> <hour> <day-of-month> <month> <day-of-week>"
    // this means to execute this command every day at 12.00 noon from monday to friday
    // rationale: office hours usually is from 9 to 5 and the employees status should be updated by 12 nn
    // For develop uncomment line 31 and comment line 32 to log every 5 seconds
//    @Scheduled(cron = "0/5 * * * * *")
    @Scheduled(cron = "0 0 12 * * MON-FRI")
    @Async
    public void logEmployeeDetails() {
        // Get all employees
        log.debug("Logging Employee Details now");
        List<Employee> employees = employeeService.getAllEmployees();

        for (Employee employee : employees) {
            employeeLogService.saveEmployeeLog(employee);
        }
        // Loop through all employees and log one by one asynchronously
    }
}

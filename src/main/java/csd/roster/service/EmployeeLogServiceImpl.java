package csd.roster.service;

import csd.roster.model.Employee;
import csd.roster.model.EmployeeLog;
import csd.roster.repository.EmployeeLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@EnableAsync
@Service
public class EmployeeLogServiceImpl implements EmployeeLogService{
    private EmployeeLogRepository employeeLogRepository;

    @Autowired
    public EmployeeLogServiceImpl(EmployeeLogRepository employeeLogRepository) {
        this.employeeLogRepository = employeeLogRepository;
    }

    @Override
    @Async
    // We can do this asynchronously since each employee is not interfering with another
    public EmployeeLog saveEmployeeLog(Employee employee) {
        return employeeLogRepository.save(new EmployeeLog(employee));
    }
}

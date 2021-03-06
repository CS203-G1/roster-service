package csd.roster.services.service;

import csd.roster.domain.model.Employee;
import csd.roster.domain.model.EmployeeLog;
import csd.roster.repo.repository.EmployeeLogRepository;
import csd.roster.services.service.interfaces.EmployeeLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@EnableAsync
@Service
public class EmployeeLogServiceImpl implements EmployeeLogService {
    private final EmployeeLogRepository employeeLogRepository;

    @Autowired
    public EmployeeLogServiceImpl(EmployeeLogRepository employeeLogRepository) {
        this.employeeLogRepository = employeeLogRepository;
    }

    @Override
    @Async
    // We can do this asynchronously since each employee is not interfering with another
    public EmployeeLog saveEmployeeLog(final Employee employee) {
        return employeeLogRepository.save(new EmployeeLog(employee));
    }
}

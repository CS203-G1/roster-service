package csd.roster.services.service.interfaces;

import csd.roster.domain.model.Employee;
import csd.roster.domain.model.EmployeeLog;

public interface EmployeeLogService {
    EmployeeLog saveEmployeeLog(Employee employee);
}

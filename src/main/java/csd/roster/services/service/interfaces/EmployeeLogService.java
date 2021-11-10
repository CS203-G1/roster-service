package csd.roster.services.service.interfaces;

import csd.roster.model.Employee;
import csd.roster.model.EmployeeLog;

public interface EmployeeLogService {
    EmployeeLog saveEmployeeLog(Employee employee);
}

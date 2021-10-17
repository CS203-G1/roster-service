package csd.roster.service.interfaces;

import csd.roster.model.Employee;
import csd.roster.model.EmployeeLog;

import java.util.UUID;

public interface EmployeeLogService {
    EmployeeLog saveEmployeeLog(Employee employee);
}

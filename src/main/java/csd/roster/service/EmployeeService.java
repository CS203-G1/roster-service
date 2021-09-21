package csd.roster.service;

import csd.roster.model.Employee;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    Employee addEmployee(UUID workLocationId, Employee employee);
    Employee getEmployee(UUID workLocationId, UUID employeeId);
    void deleteEmployee(UUID workLocationId, UUID employeeId);
    Employee updateEmployee(UUID workLocationId,UUID employeeId, Employee employee);
}

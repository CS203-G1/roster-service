package csd.roster.service;

import csd.roster.model.Employee;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    Employee addEmployee(Employee employee);
    Employee getEmployee(UUID employeeId);
    void deleteEmployee(UUID employeeId);
    Employee updateEmployee(UUID employeeId, Employee employee);
}

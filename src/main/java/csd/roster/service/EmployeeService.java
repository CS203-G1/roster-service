package csd.roster.service;

import csd.roster.model.Employee;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    Employee addEmployee(UUID departmentId, Employee employee);
    Employee getEmployee(UUID departmentId, UUID employeeId);
    Employee getEmployee(UUID employeeId);
    void deleteEmployee(UUID departmentId, UUID employeeId);
    Employee updateEmployee(UUID departmentId, UUID employeeId, Employee employee);
}

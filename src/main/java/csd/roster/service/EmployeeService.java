package csd.roster.service;

import csd.roster.model.Employee;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    Employee addEmployee(UUID companyId, UUID departmentId, Employee employee);
    Employee getEmployee(UUID companyId, UUID departmentId, UUID employeeId);
    void deleteEmployee(UUID companyId, UUID departmentId, UUID employeeId);
    Employee updateEmployee(UUID companyId, UUID departmentId, Employee employee);
}

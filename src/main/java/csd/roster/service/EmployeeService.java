package csd.roster.service;

import java.util.UUID;

import csd.roster.model.Employee;

public interface EmployeeService {
    Employee addEmployee(UUID departmentId, Employee employee);

    Employee getEmployee(UUID departmentId, UUID employeeId);

    Employee getEmployee(UUID employeeId);

    void deleteEmployee(UUID departmentId, UUID employeeId);

    Employee updateEmployee(UUID departmentId, UUID employeeId, Employee employee);

    Iterable<Employee> getAllEmployeesByCompanyId(UUID companyId);
}
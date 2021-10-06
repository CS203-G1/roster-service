package csd.roster.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import csd.roster.model.Employee;

public interface EmployeeService {
    Employee addEmployee(UUID departmentId, Employee employee);

    Employee getEmployee(UUID departmentId, UUID employeeId);

    Employee getEmployee(UUID employeeId);

    void deleteEmployee(UUID departmentId, UUID employeeId);

    Employee updateEmployee(UUID departmentId, UUID employeeId, Employee employee);

    List<Employee> getAllEmployeesByCompanyId(UUID companyId);

    List<Employee> getAllEmployees();

    List<Employee> getAllEmployeesByCompanyIdBeforeDate(UUID companyId, LocalDate date);

    List<Employee> getEmployeesOnLeaveByCompanyIdAndDate(UUID companyId, LocalDate date);
}
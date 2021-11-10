package csd.roster.services.service.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import csd.roster.domain.enumerator.HealthStatus;
import csd.roster.model.Employee;

public interface EmployeeService {
    Employee addEmployee(UUID departmentId, Employee employee);

    Employee addEmployer(UUID departmentId, Employee employee);

    Employee getEmployee(UUID departmentId, UUID employeeId);

    Employee getEmployee(UUID employeeId);

    Employee addEmployeeToWorkLocation(UUID workLocationId, UUID employeeId);

    void deleteEmployee(UUID departmentId, UUID employeeId);

    Employee updateEmployee(UUID departmentId, UUID employeeId, Employee employee);

    List<Employee> getAllEmployeesByCompanyId(UUID companyId);

    List<Employee> getAllEmployeesByWorkLocationIdAndHealthStatus(UUID workLocationId, HealthStatus healthStatus);

    List<Employee> getAllEmployees();

    List<Employee> getAllEmployeesByCompanyIdBeforeDate(UUID companyId, LocalDate date);

    List<Employee> getEmployeesOnLeaveByCompanyIdAndDate(UUID companyId, LocalDate date);

    List<Employee> getEmployeesByCompanyIdAndDateAndHealthStatus(UUID companyId,
                                                                 LocalDate date,
                                                                 HealthStatus healthStatus);

    String getEmployeeCognitoStatus(UUID employeeId);

    List<Employee> getAllEmployeesByWorkLocationId(final UUID workLocationId);
}
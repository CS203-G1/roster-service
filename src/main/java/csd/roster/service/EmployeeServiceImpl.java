package csd.roster.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import csd.roster.enumerator.HealthStatus;
import csd.roster.exception.EmployeeNotFoundException;
import csd.roster.model.Department;
import csd.roster.model.Employee;
import csd.roster.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private DepartmentService departmentService;
    private CompanyService companyService;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               DepartmentService departmentService,
                               CompanyService companyService) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
        this.companyService = companyService;
    }

    @Override
    public Employee addEmployee(UUID departmentId, Employee employee) {
        Department department = departmentService.getDepartmentById(departmentId);
        employee.setDepartment(department);
//        employee.setCompany(department.getCompany());

        return employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployee(UUID departmentId, UUID employeeId) {
        return employeeRepository.findByIdAndDepartmentId(employeeId, departmentId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    }

    // This is added and meant to be used in RosterEmployeeService
    @Override
    public Employee getEmployee(UUID employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    }

    @Override
    public void deleteEmployee(UUID departmentId, UUID employeeId) {
        Employee employee = getEmployee(departmentId, employeeId);

        employeeRepository.delete(employee);
    }

    @Override
    public Employee updateEmployee(UUID departmentId, UUID employeeId, Employee employee) {
        Department department = departmentService.getDepartmentById(departmentId);

        return employeeRepository.findByIdAndDepartmentId(employeeId, departmentId).map(oldEmployee -> {
            employee.setDepartment(department);
            employee.setId(employeeId);
            return employeeRepository.save(employee);
        }).orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    }

    @Override
    public List<Employee> getAllEmployeesByCompanyId(UUID companyId) {
        // To check if company exists
        companyService.getCompanyById(companyId);

        return employeeRepository.findAllByCompanyId(companyId);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public List<Employee> getAllEmployeesByCompanyIdBeforeDate(UUID companyId, LocalDate date) {
        companyService.getCompanyById(companyId);

        return employeeRepository.findAllByCompanyIdBeforeDate(companyId, date.atStartOfDay());
    }

    @Override
    public List<Employee> getEmployeesOnLeaveByCompanyIdAndDate(UUID companyId, LocalDate date) {
        companyService.getCompanyById(companyId);

        return employeeRepository.findAllOnLeaveByCompanyIdAndDate(companyId, date);
    }

    @Override
    public List<Employee> getEmployeesByCompanyIdAndDateAndHealthStatus(UUID companyId,
                                                                        LocalDate date,
                                                                        HealthStatus healthStatus) {
        companyService.getCompanyById(companyId);

        return employeeRepository.findAllByCompanyIdAndDateAndHealthStatus(companyId, date, healthStatus);
    }
}


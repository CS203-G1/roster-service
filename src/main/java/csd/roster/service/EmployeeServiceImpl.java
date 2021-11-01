package csd.roster.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import csd.roster.enumerator.HealthStatus;
import csd.roster.exception.exceptions.EmployeeNotFoundException;
import csd.roster.model.Department;
import csd.roster.model.Employee;
import csd.roster.model.WorkLocation;
import csd.roster.repository.EmployeeRepository;
import csd.roster.service.interfaces.CompanyService;
import csd.roster.service.interfaces.DepartmentService;
import csd.roster.service.interfaces.EmployeeService;
import csd.roster.service.interfaces.WorkLocationService;
import csd.roster.util.AwsCognitoUtil;
import csd.roster.util.AwsMailUtil;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private DepartmentService departmentService;
    private CompanyService companyService;
    private WorkLocationService workLocationService;

    private AwsMailUtil awsMailUtil;
    private AwsCognitoUtil awsCognitoUtil;

    @Value("${aws.cognito.groups.employee}")
    private String employeeGroup;
    @Value("${aws.cognito.groups.employer}")
    private String employerGroup;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               DepartmentService departmentService,
                               CompanyService companyService,
                               WorkLocationService workLocationService,
                               AwsCognitoUtil awsCognitoUtil,
                               AwsMailUtil awsMailUtil) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
        this.companyService = companyService;
        this.workLocationService = workLocationService;
        this.awsCognitoUtil = awsCognitoUtil;
        this.awsMailUtil = awsMailUtil;
    }

    @Override
    public Employee addEmployee(UUID departmentId, Employee employee) {
        employee = awsCognitoUtil.createUser(employee);

        awsCognitoUtil.addUserToGroup(employee.getId().toString(), employeeGroup);
        awsMailUtil.addEmailToPool(employee.getEmail());

        return persistEmployee(departmentId, employee);
    }

    // Violating DRY because I want to provide two endpoints for Frontend instead of having them to send in a value to
    // indicate whether the employee is employer or not
    @Override
    public Employee addEmployer(UUID departmentId, Employee employee) {
        employee = awsCognitoUtil.createUser(employee);

        awsCognitoUtil.addUserToGroup(employee.getId().toString(), employerGroup);
        awsMailUtil.addEmailToPool(employee.getEmail());

        return persistEmployee(departmentId, employee);
    }

    // Logic modularized from addEmployee and addEmployer method
    private Employee persistEmployee(UUID departmentId, Employee employee) {
        Department department = departmentService.getDepartmentById(departmentId);
        employee.setDepartment(department);
        employee.setCompany(department.getCompany());

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
    public Employee addEmployeeToWorkLocation(UUID workLocationId, UUID employeeId) {
        Employee employee = getEmployee(employeeId);
        WorkLocation workLocation = workLocationService.getWorkLocationById(workLocationId);
        employee.setWorkLocation(workLocation);

        return employeeRepository.save(employee);
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
            employee.setCompany(department.getCompany());
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
    public List<Employee> getAllEmployeesByWorkLocationIdAndHealthStatus(UUID workLocationId, HealthStatus healthStatus) {
        workLocationService.getWorkLocationById(workLocationId);

        return employeeRepository.findAllByWorkLocationIdAndHealthStatus(workLocationId, healthStatus);
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

    public String getEmployeeCognitoStatus(UUID employeeId) {
        getEmployee(employeeId);

        return awsCognitoUtil.getEmployeeCognitoStatus(employeeId);
    }
}


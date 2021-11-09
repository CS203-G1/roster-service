package csd.roster.services.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import csd.roster.domain.enumerator.HealthStatus;
import csd.roster.domain.exception.exceptions.EmployeeNotFoundException;
import csd.roster.domain.model.Department;
import csd.roster.domain.model.Employee;
import csd.roster.domain.model.WorkLocation;
import csd.roster.repo.repository.EmployeeRepository;
import csd.roster.services.service.interfaces.CompanyService;
import csd.roster.services.service.interfaces.DepartmentService;
import csd.roster.services.service.interfaces.EmailService;
import csd.roster.services.service.interfaces.EmployeeService;
import csd.roster.services.service.interfaces.WorkLocationService;
import csd.roster.repo.util.AwsCognitoUtil;
import csd.roster.util.AwsMailUtil;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final CompanyService companyService;
    private final WorkLocationService workLocationService;
    private AwsMailUtil awsMailUtil;
    private AwsCognitoUtil awsCognitoUtil;
    private EmailService emailService;


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
                               EmailService emailService) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
        this.companyService = companyService;
        this.workLocationService = workLocationService;
        this.awsCognitoUtil = awsCognitoUtil;
        this.emailService = emailService;
    }

    @Override
    public Employee addEmployee(final UUID departmentId, final Employee employee) {
        Employee createdEmployee = awsCognitoUtil.createUser(employee);
        awsCognitoUtil.addUserToGroup(createdEmployee.getId().toString(), employeeGroup);
        emailService.addEmailToPool(createdEmployee.getEmail());

        return persistEmployee(departmentId, createdEmployee);
    }

    // Violating DRY because I want to provide two endpoints for Frontend instead of having them to send in a value to
    // indicate whether the employee is employer or not
    @Override
    public Employee addEmployer(final UUID departmentId, final Employee employee) {
        Employee createdEmployee = awsCognitoUtil.createUser(employee);
        awsCognitoUtil.addUserToGroup(createdEmployee.getId().toString(), employerGroup);
        emailService.addEmailToPool(createdEmployee.getEmail());

        return persistEmployee(departmentId, createdEmployee);
    }

    // Logic modularized from addEmployee and addEmployer method
    private Employee persistEmployee(final UUID departmentId, final Employee employee) {
        Department department = departmentService.getDepartmentById(departmentId);
        employee.setDepartment(department);
        employee.setCompany(department.getCompany());

        return employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployee(final UUID departmentId, final UUID employeeId) {
        return employeeRepository.findByIdAndDepartmentId(employeeId, departmentId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    }

    // This is added and meant to be used in RosterEmployeeService
    @Override
    public Employee getEmployee(final UUID employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    }

    @Override
    public Employee addEmployeeToWorkLocation(final UUID workLocationId, final UUID employeeId) {
        Employee employee = getEmployee(employeeId);
        WorkLocation workLocation = workLocationService.getWorkLocationById(workLocationId);
        employee.setWorkLocation(workLocation);

        return employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployee(final UUID departmentId, final UUID employeeId) {
        Employee employee = getEmployee(departmentId, employeeId);

        employeeRepository.delete(employee);
    }

    @Override
    public Employee updateEmployee(final UUID departmentId, final UUID employeeId, final Employee employee) {
        Department department = departmentService.getDepartmentById(departmentId);

        return employeeRepository.findByIdAndDepartmentId(employeeId, departmentId).map(oldEmployee -> {
            employee.setDepartment(department);
            employee.setCompany(department.getCompany());
            employee.setId(employeeId);
            return employeeRepository.save(employee);
        }).orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    }

    @Override
    public List<Employee> getAllEmployeesByCompanyId(final UUID companyId) {
        // To check if company exists
        companyService.getCompanyById(companyId);

        return employeeRepository.findAllByCompanyId(companyId);
    }

    @Override
    public List<Employee> getAllEmployeesByWorkLocationIdAndHealthStatus(final UUID workLocationId,
                                                                         final HealthStatus healthStatus) {
        workLocationService.getWorkLocationById(workLocationId);

        return employeeRepository.findAllByWorkLocationIdAndHealthStatus(workLocationId, healthStatus);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public List<Employee> getAllEmployeesByCompanyIdBeforeDate(final UUID companyId, final LocalDate date) {
        companyService.getCompanyById(companyId);

        return employeeRepository.findAllByCompanyIdBeforeDate(companyId, date.atStartOfDay());
    }

    @Override
    public List<Employee> getEmployeesOnLeaveByCompanyIdAndDate(final UUID companyId, final LocalDate date) {
        companyService.getCompanyById(companyId);

        return employeeRepository.findAllOnLeaveByCompanyIdAndDate(companyId, date);
    }

    @Override
    public List<Employee> getEmployeesByCompanyIdAndDateAndHealthStatus(final UUID companyId,
                                                                        final LocalDate date,
                                                                        final HealthStatus healthStatus) {
        companyService.getCompanyById(companyId);

        return employeeRepository.findAllByCompanyIdAndDateAndHealthStatus(companyId, date, healthStatus);
    }

    public String getEmployeeCognitoStatus(final UUID employeeId) {
        getEmployee(employeeId);

        return awsCognitoUtil.getEmployeeCognitoStatus(employeeId);
    }
}


package csd.roster.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import csd.roster.util.AwsCognitoUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.roster.enumerator.HealthStatus;
import csd.roster.enumerator.VaccinationBrand;
import csd.roster.enumerator.VaccinationStatus;
import csd.roster.exception.exceptions.EmployeeNotFoundException;
import csd.roster.model.Company;
import csd.roster.model.Department;
import csd.roster.model.Employee;
import csd.roster.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    EmployeeRepository employees;

    @Mock
    DepartmentServiceImpl departmentService;

    @Mock
    AwsCognitoUtil awsCognitoUtil;

    @InjectMocks
    EmployeeServiceImpl employeeService;

    @Test
    public void addEmployee_NewEmployee_ReturnSavedEmployee(){
        UUID companyId = UUID.randomUUID();
        Company company = new Company(companyId, "Eppal", null, LocalDateTime.now());

        UUID departmentId = UUID.randomUUID();

        Department department = new Department();
        department.setId(departmentId);
        department.setCompany(company);


        UUID employeeId = UUID.randomUUID();
        VaccinationStatus vaccinationStatus = VaccinationStatus.NOT_VACCINATED;
        VaccinationBrand vaccinationBrand = VaccinationBrand.PFIZER;
        HealthStatus healthStatus = HealthStatus.COVID;

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setDepartment(department);
        employee.setVaccinationStatus(vaccinationStatus);
        employee.setVaccinationBrand(vaccinationBrand);
        employee.setHealthStatus(healthStatus);



        when(departmentService.getDepartmentById(any(UUID.class))).thenReturn(department);
        when(employees.save(any(Employee.class))).thenReturn(employee);

        Employee savedEmployee = employeeService.addEmployee(departmentId, employee);

        assertNotNull(savedEmployee);
        assertEquals(employee,savedEmployee);
        assertEquals(department, savedEmployee.getDepartment());

        verify(departmentService, times(1)).getDepartmentById(departmentId);
        verify(employees, times(1)).save(employee);
    }

    @Test
    public void getEmployee_EmployeeExists_ReturnEmployee(){
        UUID employeeId = UUID.randomUUID();
        VaccinationStatus vaccinationStatus = VaccinationStatus.NOT_VACCINATED;
        VaccinationBrand vaccinationBrand = null;
        HealthStatus healthStatus = HealthStatus.COVID;

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setVaccinationStatus(vaccinationStatus);
        employee.setVaccinationBrand(vaccinationBrand);
        employee.setHealthStatus(healthStatus);


        when(employees.findById(any(UUID.class))).thenReturn(java.util.Optional.of(employee));

        Employee foundEmployee = employeeService.getEmployee(employeeId);


        assertEquals(employee, foundEmployee);
        verify(employees, times(1)).findById(any(UUID.class));
    }

    @Test
    public void getEmployee_NullEmployee_ThrowsException(){
        UUID employeeId = UUID.randomUUID();
        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployee(employeeId));

        assertEquals("Could not find employee " + employeeId, exception.getMessage());
        verify(employees,times(1)).findById(any(UUID.class));
    }

    @Test
    public void getEmployeeByDeptIdAndEmpId_EmployeeExists_ReturnEmployee(){
        UUID companyId = UUID.randomUUID();
        Company company = new Company(companyId, "Eppal", null, LocalDateTime.now());

        UUID departmentId = UUID.randomUUID();
        Department department = new Department();
        department.setId(departmentId);
        department.setCompany(company);


        UUID employeeId = UUID.randomUUID();
        VaccinationStatus vaccinationStatus = VaccinationStatus.NOT_VACCINATED;
        VaccinationBrand vaccinationBrand = null;
        HealthStatus healthStatus = HealthStatus.COVID;

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setDepartment(department);
        employee.setVaccinationStatus(vaccinationStatus);
        employee.setVaccinationBrand(vaccinationBrand);
        employee.setHealthStatus(healthStatus);


        when(employees.findByIdAndDepartmentId(any(UUID.class), any(UUID.class))).thenReturn(java.util.Optional.of(employee));

        Employee foundEmployee = employeeService.getEmployee(departmentId,employeeId);

        assertEquals(employee,foundEmployee);

        verify(employees,times(1)).findByIdAndDepartmentId(any(UUID.class),any(UUID.class));
    }

    @Test
    public void getEmployeeByDeptIdAndEmpId_EmployeeDoesNotExist_ThrowException(){
        UUID departmentId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        Exception exception = assertThrows(EmployeeNotFoundException.class, ()-> employeeService.getEmployee(departmentId,employeeId));

        assertEquals("Could not find employee " + employeeId, exception.getMessage());
        verify(employees,times(1)).findByIdAndDepartmentId(any(UUID.class),any(UUID.class));
    }
}

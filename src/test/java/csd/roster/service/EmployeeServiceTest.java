package csd.roster.service;

import csd.roster.enumerator.HealthStatus;
import csd.roster.enumerator.VaccinationStatus;
import csd.roster.enumerator.VaccineBrand;
import csd.roster.exception.EmployeeNotFoundException;
import csd.roster.model.Company;
import csd.roster.model.Department;
import csd.roster.model.Employee;
import csd.roster.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    EmployeeRepository employees;

    @Mock
    DepartmentServiceImpl departmentService;

    @InjectMocks
    EmployeeServiceImpl employeeService;

    @Test
    public void addEmployee_NewEmployee_ReturnSavedEmployee(){
        UUID company_id = UUID.randomUUID();
        Company company = new Company(company_id, "Eppal", null);

        UUID dept_id = UUID.randomUUID();
        Department department = new Department(dept_id, company, "Marketing");

        UUID emp_id = UUID.randomUUID();
        VaccinationStatus vaccinationStatus = VaccinationStatus.NOT_VACCINATED;
        VaccineBrand vaccineBrand = null;
        HealthStatus healthStatus = HealthStatus.COVID;
        Employee employee = new Employee(emp_id, null, null, "John Doe", vaccinationStatus, vaccineBrand, healthStatus);

        when(departmentService.getDepartmentById(any(UUID.class))).thenReturn(department);
        when(employees.save(any(Employee.class))).thenReturn(employee);

        Employee savedEmployee = employeeService.addEmployee(dept_id, employee);

        assertNotNull(savedEmployee);
        assertEquals(employee,savedEmployee);
        assertEquals(department, savedEmployee.getDepartment());

        verify(departmentService, times(1)).getDepartmentById(dept_id);
        verify(employees, times(1)).save(employee);
    }

    @Test
    public void getEmployee_EmployeeExists_ReturnEmployee(){
        UUID emp_id = UUID.randomUUID();
        VaccinationStatus vaccinationStatus = VaccinationStatus.NOT_VACCINATED;
        VaccineBrand vaccineBrand = null;
        HealthStatus healthStatus = HealthStatus.COVID;
        Employee employee = new Employee(emp_id, null, null, "John Doe", vaccinationStatus, vaccineBrand, healthStatus);

        when(employees.findById(any(UUID.class))).thenReturn(java.util.Optional.of(employee));

        Employee foundEmployee = employeeService.getEmployee(emp_id);


        assertEquals(employee, foundEmployee);
        verify(employees, times(1)).findById(any(UUID.class));
    }

    @Test
    public void getEmployee_NullEmployee_ThrowsException(){
        UUID emp_id = UUID.randomUUID();
        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployee(emp_id));

        assertEquals("Could not find employee " + emp_id, exception.getMessage());
        verify(employees,times(1)).findById(any(UUID.class));
    }

    @Test
    public void getEmployeeByDeptIdAndEmpId_EmployeeExists_ReturnEmployee(){
        UUID company_id = UUID.randomUUID();
        Company company = new Company(company_id, "Eppal", null);

        UUID dept_id = UUID.randomUUID();
        Department department = new Department(dept_id, company, "Marketing");

        UUID emp_id = UUID.randomUUID();
        VaccinationStatus vaccinationStatus = VaccinationStatus.NOT_VACCINATED;
        VaccineBrand vaccineBrand = null;
        HealthStatus healthStatus = HealthStatus.COVID;
        Employee employee = new Employee(emp_id, null, department, "John Doe", vaccinationStatus, vaccineBrand, healthStatus);

        when(employees.findByIdAndDepartmentId(any(UUID.class), any(UUID.class))).thenReturn(java.util.Optional.of(employee));

        Employee foundEmployee = employeeService.getEmployee(dept_id,emp_id);

        assertEquals(employee,foundEmployee);

        verify(employees,times(1)).findByIdAndDepartmentId(any(UUID.class),any(UUID.class));
    }

    @Test
    public void getEmployeeByDeptIdAndEmpId_EmployeeDoesNotExist_ThrowException(){
        UUID dept_id = UUID.randomUUID();
        UUID emp_id = UUID.randomUUID();

        Exception exception = assertThrows(EmployeeNotFoundException.class, ()-> employeeService.getEmployee(dept_id,emp_id));

        assertEquals("Could not find employee " + emp_id, exception.getMessage());
        verify(employees,times(1)).findByIdAndDepartmentId(any(UUID.class),any(UUID.class));
    }
}

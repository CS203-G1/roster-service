package csd.roster.service;

import csd.roster.enumerator.HealthStatus;
import csd.roster.enumerator.VaccinationStatus;
import csd.roster.enumerator.VaccineBrand;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        UUID company_id = UUID.fromString("a4ccc2c4-0426-41a2-b904-f7a941ba27e0");
        Company company = new Company(company_id, "Eppal", null);

        UUID dept_id = UUID.fromString("decf52aa-94a8-48c5-abca-04f037d98e56");
        Department department = new Department(dept_id, company, "Marketing");

        UUID emp_id = UUID.fromString("adef9b37-3dd0-400f-8c11-1e5737af458f");
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
}

package csd.roster.service;

import csd.roster.exception.DepartmentNotFoundException;
import csd.roster.model.Company;
import csd.roster.model.Department;
import csd.roster.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {
    @Mock
    DepartmentRepository departments;

    @Mock
    CompanyServiceImpl companyService;

    @InjectMocks
    DepartmentServiceImpl departmentService;

    @Test
    public void addDepartment_NewDepartment_ReturnSavedDepartment(){
        UUID company_id = UUID.randomUUID();
        Company company = new Company(company_id, "Eppal", null);

        UUID dept_id = UUID.randomUUID();
        Department department = new Department(dept_id, null, "Marketing");

        when(companyService.getCompanyById(any(UUID.class))).thenReturn(company);
        when(departments.save(any(Department.class))).thenReturn(department);

        Department savedDepartment = departmentService.add(company_id, department);

        assertNotNull(savedDepartment);

        assertEquals(department,savedDepartment);

        verify(departments, times(1)).save(department);
        verify(companyService, times(1)).getCompanyById(company_id);
    }

    @Test
    public void getAllDepartments_AfterAddingThreeDepartments_ReturnListOfDepartments(){
        when(departments.findAll()).thenReturn(new ArrayList<Department>());

        List<Department> allDepartments = departmentService.getAllDepartments();

        assertNotNull(allDepartments);
        verify(departments, times(1)).findAll();


    }

    @Test
    public void getDepartmentById_DepartmentExists_ReturnFoundDepartment(){
        UUID company_id = UUID.randomUUID();
        Company company = new Company(company_id, "Eppal", null);

        UUID dept_id = UUID.randomUUID();
        Department department = new Department(dept_id, company, "Marketing");

        when(departments.findById(any(UUID.class))).thenReturn(java.util.Optional.of(department));

        Department foundDepartment = departmentService.getDepartmentById(dept_id);

        assertEquals(department,foundDepartment);
        verify(departments, times(1)).findById(dept_id);
    }

    @Test
    public void getDepartmentByIdAndCompanyId_DepartmentExists_ReturnFoundDepartment(){
        UUID company_id = UUID.randomUUID();
        Company company = new Company(company_id, "Eppal", null);

        UUID dept_id = UUID.randomUUID();
        Department department = new Department(dept_id, company, "Marketing");

        when(departments.findByIdAndCompanyId(any(UUID.class),any(UUID.class))).thenReturn(java.util.Optional.of(department));

        Department foundDepartment = departmentService.getDepartmentByIdAndCompanyId(company_id,dept_id);

        assertEquals(department, foundDepartment);
        verify(departments, times(1)).findByIdAndCompanyId(dept_id, company_id);
    }

    @Test
    public void getDepartmentByIdAndCompanyId_DepartmentDoesNotExist_ThrowException(){
        UUID company_id = UUID.randomUUID();
        Company company = new Company(company_id, "Eppal", null);

        UUID dept_id = UUID.randomUUID();
        Department department = new Department(dept_id, company, "Marketing");

        Exception exception = assertThrows(DepartmentNotFoundException.class, ()-> departmentService.getDepartmentByIdAndCompanyId(company_id,dept_id));

        String expectedExceptionMessage = String.format("Could not find department %s from company %s", dept_id, company_id);
        assertEquals(expectedExceptionMessage, exception.getMessage());
        verify(departments, times(1)).findByIdAndCompanyId(dept_id, company_id);

    }
}
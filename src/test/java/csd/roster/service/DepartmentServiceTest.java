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
        UUID companyId = UUID.randomUUID();
        Company company = new Company(companyId, "Eppal", null);

        UUID departmentId = UUID.randomUUID();
        Department department = new Department(departmentId, null, null, "Marketing");

        when(companyService.getCompanyById(any(UUID.class))).thenReturn(company);
        when(departments.save(any(Department.class))).thenReturn(department);

        Department savedDepartment = departmentService.add(companyId, department);

        assertNotNull(savedDepartment);

        assertEquals(department,savedDepartment);

        verify(departments, times(1)).save(department);
        verify(companyService, times(1)).getCompanyById(companyId);
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
        UUID companyId = UUID.randomUUID();
        Company company = new Company(companyId, "Eppal", null);

        UUID departmentId = UUID.randomUUID();
        Department department = new Department(departmentId, company, null, "Marketing");

        when(departments.findById(any(UUID.class))).thenReturn(java.util.Optional.of(department));

        Department foundDepartment = departmentService.getDepartmentById(departmentId);

        assertEquals(department,foundDepartment);
        verify(departments, times(1)).findById(departmentId);
    }

    @Test
    public void getDepartmentByIdAndCompanyId_DepartmentExists_ReturnFoundDepartment(){
        UUID companyId = UUID.randomUUID();
        Company company = new Company(companyId, "Eppal", null);

        UUID departmentId = UUID.randomUUID();
        Department department = new Department(departmentId, company, null, "Marketing");

        when(departments.findByIdAndCompanyId(any(UUID.class),any(UUID.class))).thenReturn(java.util.Optional.of(department));

        Department foundDepartment = departmentService.getDepartmentByIdAndCompanyId(departmentId, companyId);

        assertEquals(department, foundDepartment);
        verify(departments, times(1)).findByIdAndCompanyId(departmentId, companyId);
    }

    @Test
    public void getDepartmentByIdAndCompanyId_DepartmentDoesNotExist_ThrowException(){
        UUID companyId = UUID.randomUUID();
        Company company = new Company(companyId, "Eppal", null);

        UUID departmentId = UUID.randomUUID();
        Department department = new Department(departmentId, company, null, "Marketing");

        Exception exception = assertThrows(DepartmentNotFoundException.class,
                () -> departmentService.getDepartmentByIdAndCompanyId(departmentId, companyId));

        String expectedExceptionMessage = String.format("Could not find department %s from company %s", departmentId, companyId);
        assertEquals(expectedExceptionMessage, exception.getMessage());
        verify(departments, times(1)).findByIdAndCompanyId(departmentId, companyId);

    }
}

package csd.roster.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.roster.exception.exceptions.DepartmentNotFoundException;
import csd.roster.model.Company;
import csd.roster.model.Department;
import csd.roster.repository.DepartmentRepository;

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
        Company company = new Company(companyId, "Eppal", null, LocalDateTime.now());

        UUID departmentId = UUID.randomUUID();
        Department department = new Department(departmentId, null, null, "Marketing", LocalDateTime.now());

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
        Company company = new Company(companyId, "Eppal", null, null);

        UUID departmentId = UUID.randomUUID();
        Department department = new Department(departmentId, company, null, "Marketing", LocalDateTime.now());

        when(departments.findById(any(UUID.class))).thenReturn(java.util.Optional.of(department));

        Department foundDepartment = departmentService.getDepartmentById(departmentId);

        assertEquals(department,foundDepartment);
        verify(departments, times(1)).findById(departmentId);
    }

    @Test
    public void getDepartmentByIdAndCompanyId_DepartmentExists_ReturnFoundDepartment(){
        UUID companyId = UUID.randomUUID();
        Company company = new Company(companyId, "Eppal", null, LocalDateTime.now());

        UUID departmentId = UUID.randomUUID();
        Department department = new Department(departmentId, company,null,  "Marketing", LocalDateTime.now());

        when(departments.findByIdAndCompanyId(any(UUID.class),any(UUID.class))).thenReturn(java.util.Optional.of(department));

        Department foundDepartment = departmentService.getDepartmentByIdAndCompanyId(departmentId, companyId);

        assertEquals(department, foundDepartment);
        verify(departments, times(1)).findByIdAndCompanyId(departmentId, companyId);
    }

    @Test
    public void getDepartmentByIdAndCompanyId_DepartmentDoesNotExist_ThrowException(){
        UUID companyId = UUID.randomUUID();
        Company company = new Company(companyId, "Eppal", null, LocalDateTime.now());

        UUID departmentId = UUID.randomUUID();
        Department department = new Department(departmentId, company, null, "Marketing", LocalDateTime.now());

        Exception exception = assertThrows(DepartmentNotFoundException.class,
                () -> departmentService.getDepartmentByIdAndCompanyId(departmentId, companyId));

        String expectedExceptionMessage = String.format("Could not find department %s from company %s", departmentId, companyId);
        assertEquals(expectedExceptionMessage, exception.getMessage());
        verify(departments, times(1)).findByIdAndCompanyId(departmentId, companyId);

    }
}

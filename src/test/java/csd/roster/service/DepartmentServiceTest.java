package csd.roster.service;

import csd.roster.model.Company;
import csd.roster.model.Department;
import csd.roster.repository.DepartmentRepository;
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
public class DepartmentServiceTest {
    @Mock
    DepartmentRepository departments;

    @Mock
    CompanyServiceImpl companyService;

    @InjectMocks
    DepartmentServiceImpl departmentService;

    @Test
    public void addDepartment_NewDepartment_ReturnSavedDepartment(){
        UUID company_id = UUID.fromString("a4ccc2c4-0426-41a2-b904-f7a941ba27e0");
        Company company = new Company(company_id, "Eppal", null);

        UUID dept_id = UUID.fromString("decf52aa-94a8-48c5-abca-04f037d98e56");
        Department department = new Department(dept_id, null, "Marketing");

        when(companyService.getCompanyById(any(UUID.class))).thenReturn(company);
        when(departments.save(any(Department.class))).thenReturn(department);

        Department savedDepartment = departmentService.add(company_id, department);

        assertNotNull(savedDepartment);

        assertEquals(department,savedDepartment);

        verify(departments, times(1)).save(department);
        verify(companyService, times(1)).getCompanyById(company_id);
    }
}

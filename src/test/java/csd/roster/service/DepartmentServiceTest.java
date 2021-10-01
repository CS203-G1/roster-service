package csd.roster.service;

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

    @Test
    public void getAllDepartments_AfterAddingThreeDepartments_ReturnListOfDepartments(){
        UUID company_id = UUID.fromString("a4ccc2c4-0426-41a2-b904-f7a941ba27e0");
        Company company = new Company(company_id, "Eppal", null);

        UUID dept1_id = UUID.fromString("decf52aa-94a8-48c5-abca-04f037d98e56");
        Department department1 = new Department(dept1_id, null, "Marketing");

        UUID dept2_id = UUID.fromString("0fb6f2a1-597b-42da-9c69-4b2d766dce46");
        Department department2 = new Department(dept2_id, null, "Software Engineering");

        UUID dept3_id = UUID.fromString("0d7a075f-a35e-4b5c-b960-6f4b47df1ce7");
        Department department3 = new Department(dept3_id, null, "Human Resources");

        when(companyService.getCompanyById(any(UUID.class))).thenReturn(company);
        when(departments.findAll()).thenReturn(new ArrayList<Department>());
        when(departments.save(any(Department.class)))
                .thenReturn(department1)
                .thenReturn(department2)
                .thenReturn(department3);

        Department savedDepartment1 = departmentService.add(company_id, department1);
        Department savedDepartment2 = departmentService.add(company_id, department2);
        Department savedDepartment3 = departmentService.add(company_id, department3);
        List<Department> allDepartments = departmentService.getAllDepartments();

        assertEquals(department1, savedDepartment1);
        assertEquals(department2, savedDepartment2);
        assertEquals(department3, savedDepartment3);
        assertNotNull(allDepartments);

        verify(departments, times(3)).save(any(Department.class));
        verify(departments, times(1)).findAll();


    }
}

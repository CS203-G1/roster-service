package csd.roster.service;

import csd.roster.exception.WorkLocationNotFoundException;
import csd.roster.model.Company;
import csd.roster.model.Department;
import csd.roster.model.WorkLocation;
import csd.roster.repository.WorkLocationRepository;
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
public class WorkLocationServiceTest {
    @Mock
    WorkLocationRepository workLocations;

    @Mock
    DepartmentServiceImpl departmentService;

    @InjectMocks
    WorkLocationServiceImpl workLocationService;

    @Test
    public void addWorkLocation_NewWorkLocation_ReturnSavedWorkLocation(){
        UUID company_id = UUID.randomUUID();
        Company company = new Company(company_id, "Eppal", null);

        UUID dept_id = UUID.randomUUID();
        Department department = new Department(dept_id, company, "Marketing");

        WorkLocation workLocation = new WorkLocation(null, null, "Eppal Headquarter", "7 Jalan Naga Sari", 40, null);

        when(departmentService.getDepartmentByIdAndCompanyId(any(UUID.class), any(UUID.class))).thenReturn(department);
        when(workLocations.save(any(WorkLocation.class))).thenReturn(workLocation);

        WorkLocation savedWorkLocation = workLocationService.add(company_id,dept_id, workLocation);

        assertNotNull(savedWorkLocation);
        assertEquals(department, workLocation.getDepartment());

        verify(workLocations, times(1)).save(workLocation);
        verify(departmentService, times(1)).getDepartmentByIdAndCompanyId(company_id,dept_id);
    }

    @Test
    public void get_WorkLocationExists_ReturnFoundWorkLocation(){
        UUID company_id = UUID.randomUUID();
        Company company = new Company(company_id, "Eppal", null);

        UUID dept_id = UUID.randomUUID();
        Department department = new Department(dept_id, company, "Marketing");

        UUID work_location_id = UUID.randomUUID();
        WorkLocation workLocation = new WorkLocation(work_location_id, department, "Eppal Headquarter", "7 Jalan Naga Sari", 40, null);

        when(departmentService.getDepartmentByIdAndCompanyId(any(UUID.class), any(UUID.class))).thenReturn(department);
        when(workLocations.findByIdAndDepartmentId(work_location_id, dept_id)).thenReturn(java.util.Optional.of(workLocation));

        WorkLocation foundWorkLocation = workLocationService.get(company_id, dept_id, work_location_id);

        assertEquals(workLocation, foundWorkLocation);

        verify(departmentService, times(1)).getDepartmentByIdAndCompanyId(company_id, dept_id);
        verify(workLocations,times(1)).findByIdAndDepartmentId(work_location_id,dept_id);

    }

    @Test
    public void get_WorkLocationDoesNotExist_ThrowException(){
        UUID company_id = UUID.randomUUID();
        UUID dept_id = UUID.randomUUID();
        UUID work_location_id = UUID.randomUUID();

        Exception exception = assertThrows(WorkLocationNotFoundException.class, () -> workLocationService.get(company_id,dept_id,work_location_id));

        String expectedExceptionMessage = String.format("Could not find work location %s from department %s from company %s",
                work_location_id,
                dept_id,
                company_id);
        assertEquals(expectedExceptionMessage, exception.getMessage());
        verify(departmentService, times(1)).getDepartmentByIdAndCompanyId(company_id, dept_id);
        verify(workLocations,times(1)).findByIdAndDepartmentId(work_location_id,dept_id);
    }

    @Test
    public void getWorkLocationById_WorkLocationExists_ReturnFoundWorkLocation(){
        UUID company_id = UUID.randomUUID();
        Company company = new Company(company_id, "Eppal", null);

        UUID dept_id = UUID.randomUUID();
        Department department = new Department(dept_id, company, "Marketing");

        UUID work_location_id = UUID.randomUUID();
        WorkLocation workLocation = new WorkLocation(work_location_id, department, "Eppal Headquarter", "7 Jalan Naga Sari", 40, null);

        when(workLocations.findById(work_location_id)).thenReturn(java.util.Optional.of(workLocation));

        WorkLocation foundWorkLocation = workLocationService.getWorkLocationById(work_location_id);

        assertEquals(workLocation, foundWorkLocation);
        verify(workLocations, times(1)).findById(work_location_id);
    }

    @Test
    public void getWorkLocationById_WorkLocationDoesNotExist_ThrowException(){
        UUID work_location_id = UUID.randomUUID();

        Exception exception = assertThrows(WorkLocationNotFoundException.class, () -> workLocationService.getWorkLocationById(work_location_id));
        String expectedExceptionMessage = String.format("Could not find work location %s", work_location_id);
        assertEquals(expectedExceptionMessage, exception.getMessage());

        verify(workLocations, times(1)).findById(work_location_id);
    }

}

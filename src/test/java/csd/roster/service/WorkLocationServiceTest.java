package csd.roster.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.roster.exception.exceptions.WorkLocationNotFoundException;
import csd.roster.model.Company;
import csd.roster.model.Department;
import csd.roster.model.WorkLocation;
import csd.roster.repository.WorkLocationRepository;

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
        UUID companyId = UUID.randomUUID();
        Company company = new Company(companyId, "Eppal", null, null);

        UUID departmentId = UUID.randomUUID();
        Department department = new Department();
        department.setId(departmentId);
        department.setCompany(company);


        WorkLocation workLocation = new WorkLocation(null, null, null, null, "Eppal Headquarter", "7 Jalan Naga Sari", 40);

        when(departmentService.getDepartmentByIdAndCompanyId(any(UUID.class), any(UUID.class))).thenReturn(department);
        when(workLocations.save(any(WorkLocation.class))).thenReturn(workLocation);

        WorkLocation savedWorkLocation = workLocationService.add(companyId,departmentId, workLocation);

        assertNotNull(savedWorkLocation);
        assertEquals(department, workLocation.getDepartment());

        verify(workLocations, times(1)).save(workLocation);
        verify(departmentService, times(1)).getDepartmentByIdAndCompanyId(departmentId, companyId);
    }

    @Test
    public void get_WorkLocationExists_ReturnFoundWorkLocation(){
        UUID companyId = UUID.randomUUID();
        Company company = new Company(companyId, "Eppal", null, null);

        UUID departmentId = UUID.randomUUID();

        Department department = new Department();
        department.setId(departmentId);
        department.setCompany(company);


        UUID workLocationId = UUID.randomUUID();
        WorkLocation workLocation = new WorkLocation(workLocationId, department, null, null, "Eppal Headquarter", "7 Jalan Naga Sari", 40);

        when(departmentService.getDepartmentByIdAndCompanyId(any(UUID.class), any(UUID.class))).thenReturn(department);
        when(workLocations.findByIdAndDepartmentId(workLocationId, departmentId)).thenReturn(java.util.Optional.of(workLocation));

        WorkLocation foundWorkLocation = workLocationService.get(companyId, departmentId, workLocationId);

        assertEquals(workLocation, foundWorkLocation);

        verify(departmentService, times(1)).getDepartmentByIdAndCompanyId(departmentId, companyId);
        verify(workLocations,times(1)).findByIdAndDepartmentId(workLocationId,departmentId);

    }

    @Test
    public void get_WorkLocationDoesNotExist_ThrowException(){
        UUID companyId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        UUID workLocationId = UUID.randomUUID();

        Exception exception = assertThrows(WorkLocationNotFoundException.class, () -> workLocationService.get(companyId,departmentId,workLocationId));

        String expectedExceptionMessage = String.format("Could not find work location %s from department %s from company %s",
                workLocationId,
                departmentId,
                companyId);
        assertEquals(expectedExceptionMessage, exception.getMessage());
        verify(departmentService, times(1)).getDepartmentByIdAndCompanyId(departmentId, companyId);
        verify(workLocations,times(1)).findByIdAndDepartmentId(workLocationId,departmentId);
    }

    @Test
    public void getWorkLocationById_WorkLocationExists_ReturnFoundWorkLocation(){
        UUID companyId = UUID.randomUUID();
        Company company = new Company(companyId, "Eppal", null, null);

        UUID departmentId = UUID.randomUUID();
        Department department = new Department();
        department.setId(departmentId);
        department.setCompany(company);


        UUID workLocationId = UUID.randomUUID();
        WorkLocation workLocation = new WorkLocation(workLocationId, department, null, null, "Eppal Headquarter", "7 Jalan Naga Sari", 40);

        when(workLocations.findById(workLocationId)).thenReturn(Optional.of(workLocation));

        WorkLocation foundWorkLocation = workLocationService.getWorkLocationById(workLocationId);

        assertEquals(workLocation, foundWorkLocation);
        verify(workLocations, times(1)).findById(workLocationId);
    }

    @Test
    public void getWorkLocationById_WorkLocationDoesNotExist_ThrowException(){
        UUID workLocationId = UUID.randomUUID();

        Exception exception = assertThrows(WorkLocationNotFoundException.class, () -> workLocationService.getWorkLocationById(workLocationId));
        String expectedExceptionMessage = String.format("Could not find work location %s", workLocationId);
        assertEquals(expectedExceptionMessage, exception.getMessage());

        verify(workLocations, times(1)).findById(workLocationId);
    }

}

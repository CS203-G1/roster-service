package csd.roster.service;

import csd.roster.enumerator.HealthStatus;
import csd.roster.enumerator.VaccinationStatus;
import csd.roster.enumerator.VaccineBrand;
import csd.roster.exception.EmployeeNotHealthyException;
import csd.roster.model.Employee;
import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;
import csd.roster.repository.RosterEmployeeRepository;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RosterEmployeeServiceTest {
    @Mock
    RosterServiceImpl rosterService;

    @Mock
    EmployeeServiceImpl employeeService;

    @Mock
    RosterEmployeeRepository rosterEmployees;

    @InjectMocks
    RosterEmployeeServiceImpl rosterEmployeeService;

    @Test
    public void addRosterEmployee_ValidRosterEmployee_ReturnSavedRosterEmployee(){
        UUID rosterId = UUID.randomUUID();
        Roster roster = new Roster(rosterId, LocalDate.now(), null, null);

        UUID employeeId = UUID.randomUUID();
        VaccinationStatus vaccinationStatus = VaccinationStatus.SECOND_DOSE;
        VaccineBrand vaccineBrand = VaccineBrand.PFIZER;
        HealthStatus healthStatus = HealthStatus.HEALTHY;
        Employee employee = new Employee(employeeId, null, null, "John Doe", vaccinationStatus, vaccineBrand, healthStatus);

        UUID rosterEmployeeId = UUID.randomUUID();
        LocalDateTime fromTime = LocalDateTime.of(2023, 12, 12, 9,0,0);
        LocalDateTime toTime = LocalDateTime.of(2023, 12, 12, 17,0,0);
        RosterEmployee rosterEmployee = new RosterEmployee(rosterEmployeeId, null, null, fromTime, toTime, null);

        when(employeeService.getEmployee(employeeId)).thenReturn(employee);
        when(rosterService.getRoster(rosterId)).thenReturn(roster);
        when(rosterEmployees.save(rosterEmployee)).thenReturn(rosterEmployee);

        RosterEmployee savedRosterEmployee = rosterEmployeeService.addRosterEmployee(rosterId, employeeId, rosterEmployee);

        assertEquals(employee, savedRosterEmployee.getEmployee());
        assertEquals(roster, savedRosterEmployee.getRoster());

        verify(employeeService, times(1)).getEmployee(employeeId);
        verify(rosterService,times(1)).getRoster(rosterId);
        verify(rosterEmployees, times(1)).save(rosterEmployee);
    }

    @Test
    public void addRosterEmployee_UnhealthyEmployee_ReturnSavedRosterEmployee(){
        UUID rosterId = UUID.randomUUID();
        Roster roster = new Roster(rosterId, LocalDate.now(), null, null);

        UUID employeeId = UUID.randomUUID();
        VaccinationStatus vaccinationStatus = VaccinationStatus.SECOND_DOSE;
        VaccineBrand vaccineBrand = VaccineBrand.PFIZER;
        HealthStatus healthStatus = HealthStatus.COVID;
        Employee employee = new Employee(employeeId, null, null, "John Doe", vaccinationStatus, vaccineBrand, healthStatus);

        UUID rosterEmployeeId = UUID.randomUUID();
        LocalDateTime fromTime = LocalDateTime.of(2023, 12, 12, 9,0,0);
        LocalDateTime toTime = LocalDateTime.of(2023, 12, 12, 17,0,0);
        RosterEmployee rosterEmployee = new RosterEmployee(rosterEmployeeId, null, null, fromTime, toTime, null);

        when(employeeService.getEmployee(employeeId)).thenReturn(employee);
        when(rosterService.getRoster(rosterId)).thenReturn(roster);

        Exception exception = assertThrows(EmployeeNotHealthyException.class, () -> rosterEmployeeService.addRosterEmployee(rosterId, employeeId, rosterEmployee));
        String expectedExceptionMessage = String.format("Employee %s is not healthy", employee.getId());

        assertEquals(expectedExceptionMessage, exception.getMessage());
        verify(employeeService, times(1)).getEmployee(employeeId);
        verify(rosterService,times(1)).getRoster(rosterId);

    }
    
}

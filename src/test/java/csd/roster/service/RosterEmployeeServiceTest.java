package csd.roster.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.roster.enumerator.HealthStatus;
import csd.roster.enumerator.VaccinationBrand;
import csd.roster.enumerator.VaccinationStatus;
import csd.roster.exception.EmployeeNotHealthyException;
import csd.roster.exception.RosterEmployeeNotFoundException;
import csd.roster.model.Employee;
import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;
import csd.roster.repository.RosterEmployeeRepository;

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
        VaccinationBrand vaccinationBrand = VaccinationBrand.PFIZER;
        HealthStatus healthStatus = HealthStatus.HEALTHY;
        Employee employee = new Employee(employeeId, null, null, "John Doe", vaccinationStatus, vaccinationBrand, healthStatus, new Date(), true);

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
        VaccinationBrand vaccinationBrand = VaccinationBrand.PFIZER;
        HealthStatus healthStatus = HealthStatus.COVID;
        Employee employee = new Employee(employeeId, null, null, "John Doe", vaccinationStatus, vaccinationBrand, healthStatus, new Date(), true);

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

    @Test
    public void getRosterEmployee_RosterEmployeeExists_ReturnFoundRosterEmployee(){
        UUID rosterId = UUID.randomUUID();
        Roster roster = new Roster(rosterId, LocalDate.now(), null, null);

        UUID employeeId = UUID.randomUUID();
        VaccinationStatus vaccinationStatus = VaccinationStatus.SECOND_DOSE;
        VaccinationBrand vaccinationBrand = VaccinationBrand.PFIZER;
        HealthStatus healthStatus = HealthStatus.COVID;
        Employee employee = new Employee(employeeId, null, null, "John Doe", vaccinationStatus, vaccinationBrand, healthStatus, new Date(), true);

        UUID rosterEmployeeId = UUID.randomUUID();
        LocalDateTime fromTime = LocalDateTime.of(2023, 12, 12, 9,0,0);
        LocalDateTime toTime = LocalDateTime.of(2023, 12, 12, 17,0,0);
        RosterEmployee rosterEmployee = new RosterEmployee(rosterEmployeeId, roster, employee, fromTime, toTime, null);

        when(rosterEmployees.findByRosterIdAndEmployeeId(rosterId, employeeId)).thenReturn(java.util.Optional.of(rosterEmployee));

        RosterEmployee foundRosterEmployee = rosterEmployeeService.getRosterEmployee(rosterId,employeeId);

        assertEquals(rosterEmployee ,foundRosterEmployee);
        verify(rosterEmployees,times(1)).findByRosterIdAndEmployeeId(rosterId,employeeId);
    }

    @Test
    public void getRosterEmployee_RosterEmployeeDoesNotExist_ThrowException(){
        UUID rosterId = UUID.randomUUID();
        Roster roster = new Roster(rosterId, LocalDate.now(), null, null);

        UUID employeeId = UUID.randomUUID();
        VaccinationStatus vaccinationStatus = VaccinationStatus.SECOND_DOSE;
        VaccinationBrand vaccinationBrand = VaccinationBrand.PFIZER;
        HealthStatus healthStatus = HealthStatus.COVID;
        Employee employee = new Employee(employeeId, null, null, "John Doe", vaccinationStatus, vaccinationBrand, healthStatus, new Date(), true);

        Exception exception = assertThrows(RosterEmployeeNotFoundException.class, () -> rosterEmployeeService.getRosterEmployee(rosterId,employeeId));

        String expectedExceptionMessage = String.format("Employee %s is not is Roster %s", employeeId, rosterId);

        assertEquals(expectedExceptionMessage, exception.getMessage());
        verify(rosterEmployees,times(1)).findByRosterIdAndEmployeeId(rosterId,employeeId);
    }
}

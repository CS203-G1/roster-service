package test.csd.roster.service;

import csd.roster.model.Company;
import csd.roster.model.Employee;
import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;
import csd.roster.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkStatisticsServiceTest {

    @Mock
    private CompanyServiceImpl companyService;

    @Mock
    private RosterEmployeeServiceImpl rosterEmployeeService;

    @Mock
    private EmployeeServiceImpl employeeService;

    @Mock
    private EmployeeLogServiceImpl employeeLogsService;

    @InjectMocks
    private WorkStatisticsServiceImpl workStatisticsService;

    @Test
    void getOnsiteEmployeesListByCompanyAndDate() {
        UUID companyId = UUID.randomUUID();
        Company company = new Company();
        company.setId(companyId);

        List<RosterEmployee> rosterEmployeeList = new ArrayList<RosterEmployee>();

        RosterEmployee rosterEmployee1 = new RosterEmployee();
        Employee employee1 = new Employee();
        rosterEmployee1.setEmployee(employee1);

        RosterEmployee rosterEmployee2 = new RosterEmployee();
        Employee employee2 = new Employee();
        rosterEmployee2.setEmployee(employee2);

        rosterEmployeeList.add(rosterEmployee1);
        rosterEmployeeList.add(rosterEmployee2);

        LocalDate date = LocalDate.now();

        when(companyService.getCompanyById(any(UUID.class))).thenReturn(company);
        when(rosterEmployeeService.findOnsiteRosterEmployeesByCompanyIdAndDate(any(UUID.class),any(LocalDate.class))).thenReturn(rosterEmployeeList);


        List<Employee> foundOnsiteEmployees = workStatisticsService.getOnsiteEmployeesListByCompanyAndDate(companyId, date);

        assertEquals(2, foundOnsiteEmployees.size());
        assertEquals(employee1,foundOnsiteEmployees.get(0));
        assertEquals(employee2, foundOnsiteEmployees.get(1));

        verify(companyService, times(1)).getCompanyById(companyId);
        verify(rosterEmployeeService, times(1)).findOnsiteRosterEmployeesByCompanyIdAndDate(companyId, date);


    }

    @Test
    void getWorkStatisticsByCompanyAndDateRange() {
    }

    @Test
    void getWorkStatisticsByCompanyAndDate() {
    }

    @Test
    void getSummaryByCompanyIdAndDate() {
    }

    @Test
    void getSummaryByEmployerIdAndDate() {
    }
}
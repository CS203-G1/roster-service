package test.csd.roster.service;

import csd.roster.model.Company;
import csd.roster.model.Employee;
import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;
import csd.roster.response_model.WorkingStatisticResponseModel;
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
    void getWorkStatisticsByCompanyAndDateRange_NoRosterEmployees_returnEmptyWorkStatisticResponseModel() {
        UUID companyId = UUID.randomUUID();
        Company company = new Company();
        company.setId(companyId);

        LocalDate fromDate = LocalDate.ofYearDay(2020,150);
        LocalDate toDate = LocalDate.ofYearDay(2020,152);

        when(companyService.getCompanyById(any(UUID.class))).thenReturn(company);
        when(rosterEmployeeService.findAllRosterEmployeesByCompanyIdAndDate(any(UUID.class), any(LocalDate.class))).thenReturn(new ArrayList<RosterEmployee>());
        // when(rosterEmployeeService.findRemoteRosterEmployeesByCompanyIdAndDate(any(UUID.class), any(LocalDate.class))).thenReturn(new ArrayList<RosterEmployee>());

        List<WorkingStatisticResponseModel> workingStatisticResponseModelList = workStatisticsService.getWorkStatisticsByCompanyAndDateRange(companyId, fromDate, toDate);
        assertEquals(3, workingStatisticResponseModelList.size());
        for (WorkingStatisticResponseModel w: workingStatisticResponseModelList) {
            assertEquals(companyId, w.getCompanyId());
            assertEquals(0, w.getOnsiteCount());
            assertEquals(0, w.getRemoteCount());
        }

        verify(companyService, times(3)).getCompanyById(companyId);
        verify(rosterEmployeeService, times(3)).findAllRosterEmployeesByCompanyIdAndDate(eq(companyId), any(LocalDate.class));
    }

    @Test
    void getWorkStatisticsByCompanyAndDateRange_OneOnsiteRosterEmployees_returnEmptyWorkStatisticResponseModel() {
        UUID companyId = UUID.randomUUID();
        Company company = new Company();
        company.setId(companyId);

        List<RosterEmployee> rosterEmployeeList = new ArrayList<RosterEmployee>();

        RosterEmployee rosterEmployee1 = new RosterEmployee();
        Employee employee1 = new Employee();
        rosterEmployee1.setEmployee(employee1);
        rosterEmployeeList.add(rosterEmployee1);

        LocalDate fromDate = LocalDate.ofYearDay(2020,150);
        LocalDate toDate = LocalDate.ofYearDay(2020,152);

        when(companyService.getCompanyById(any(UUID.class))).thenReturn(company);
        when(rosterEmployeeService.findAllRosterEmployeesByCompanyIdAndDate(any(UUID.class), any(LocalDate.class))).thenReturn(rosterEmployeeList);
        when(rosterEmployeeService.findRemoteRosterEmployeesByCompanyIdAndDate(any(UUID.class), any(LocalDate.class))).thenReturn(new ArrayList<RosterEmployee>());

        List<WorkingStatisticResponseModel> workingStatisticResponseModelList = workStatisticsService.getWorkStatisticsByCompanyAndDateRange(companyId, fromDate, toDate);
        assertEquals(3, workingStatisticResponseModelList.size());
        for (WorkingStatisticResponseModel w: workingStatisticResponseModelList) {
            assertEquals(companyId, w.getCompanyId());
            assertEquals(1, w.getOnsiteCount());
            assertEquals(0, w.getRemoteCount());
        }

        verify(companyService, times(3)).getCompanyById(companyId);
        verify(rosterEmployeeService, times(3)).findAllRosterEmployeesByCompanyIdAndDate(eq(companyId), any(LocalDate.class));
        verify(rosterEmployeeService, times(3)).findRemoteRosterEmployeesByCompanyIdAndDate(eq(companyId), any(LocalDate.class));
    }


    @Test
    void getWorkStatisticsByCompanyAndDate_returnWorkStatisticResponseModel() {
        UUID companyId = UUID.randomUUID();
        Company company = new Company();
        company.setId(companyId);

        // Mocking Remote RosterEmployees
        List<RosterEmployee> remoteRosterEmployeeList = new ArrayList<RosterEmployee>();

        RosterEmployee rosterEmployee3 = new RosterEmployee();
        Employee employee3 = new Employee();
        rosterEmployee3.setEmployee(employee3);

        RosterEmployee rosterEmployee4 = new RosterEmployee();
        Employee employee4 = new Employee();
        rosterEmployee4.setEmployee(employee4);

        remoteRosterEmployeeList.add(rosterEmployee3);
        remoteRosterEmployeeList.add(rosterEmployee4);


        // Mocking All RosterEmployees
        List<RosterEmployee> rosterEmployeeList = new ArrayList<RosterEmployee>();

        RosterEmployee rosterEmployee1 = new RosterEmployee();
        Employee employee1 = new Employee();
        rosterEmployee1.setEmployee(employee1);

        RosterEmployee rosterEmployee2 = new RosterEmployee();
        Employee employee2 = new Employee();
        rosterEmployee2.setEmployee(employee2);

        rosterEmployeeList.add(rosterEmployee1);
        rosterEmployeeList.add(rosterEmployee2);
        rosterEmployeeList.add(rosterEmployee3);
        rosterEmployeeList.add(rosterEmployee4);

        LocalDate date = LocalDate.now();

        when(companyService.getCompanyById(any(UUID.class))).thenReturn(company);
        when(rosterEmployeeService.findAllRosterEmployeesByCompanyIdAndDate(any(UUID.class), any(LocalDate.class))).thenReturn(rosterEmployeeList);
        when(rosterEmployeeService.findRemoteRosterEmployeesByCompanyIdAndDate(any(UUID.class), any(LocalDate.class))).thenReturn(remoteRosterEmployeeList);

        WorkingStatisticResponseModel workingStatisticResponseModel = workStatisticsService.getWorkStatisticsByCompanyAndDate(companyId, date);

        assertEquals(companyId, workingStatisticResponseModel.getCompanyId());
        assertEquals(2, workingStatisticResponseModel.getOnsiteCount());
        assertEquals(2, workingStatisticResponseModel.getRemoteCount());

        verify(companyService, times(1)).getCompanyById(companyId);
        verify(rosterEmployeeService, times(1)).findAllRosterEmployeesByCompanyIdAndDate(companyId, date);
        verify(rosterEmployeeService, times(1)).findRemoteRosterEmployeesByCompanyIdAndDate(companyId, date);
    }

    @Test
    void getSummaryByCompanyIdAndDate() {
    }

    @Test
    void getSummaryByEmployerIdAndDate() {
    }
}
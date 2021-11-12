package csd.roster.services.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import csd.roster.domain.enumerator.HealthStatus;
import csd.roster.domain.model.*;
import csd.roster.domain.response_model.SummaryResponseModel;
import csd.roster.domain.response_model.WorkingStatisticResponseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.roster.domain.exception.exceptions.WorkLocationNotFoundException;
import csd.roster.repo.repository.WorkLocationRepository;

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
        Department department = new Department(departmentId,
                company,
                null,
                null,
                "Marketing",
                null);

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
        Department department = new Department(departmentId, company,null, null, "Marketing", null);

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
        Department department = new Department(departmentId, company,null, null, "Marketing", null);

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

    @ExtendWith(MockitoExtension.class)
    static
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
        void getOnsiteEmployeesListByCompanyAndDate_returnListOfTwoEmployees() {
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
        void getSummaryByCompanyIdAndDate_returnSummaryOfCompany() {
            UUID companyId = UUID.randomUUID();
            Company company = new Company();
            company.setId(companyId);

            Employee employee1 = new Employee();
            Employee employee2 = new Employee();
            Employee employee3 = new Employee();
            Employee employee4 = new Employee();
            Employee employee5 = new Employee();
            Employee employee6 = new Employee();
            Employee employee7 = new Employee();

            // Mocking the various lists of employees
            List<Employee> employeeList = new ArrayList<>();
            employeeList.add(employee1);
            employeeList.add(employee2);
            employeeList.add(employee3);
            employeeList.add(employee4);
            employeeList.add(employee5);
            employeeList.add(employee6);
            employeeList.add(employee7);

            List<Employee> employeesOnLeave = new ArrayList<>();
            employeesOnLeave.add(employee1);

            List<Employee> employeesWithCovid = new ArrayList<>();
            employeesWithCovid.add(employee2);
            employeesWithCovid.add(employee3);

            List<RosterEmployee> onsiteEmployees = new ArrayList<>();
            RosterEmployee rosterEmployee4 = new RosterEmployee();
            rosterEmployee4.setEmployee(employee4);
            RosterEmployee rosterEmployee5 = new RosterEmployee();
            rosterEmployee4.setEmployee(employee5);
            RosterEmployee rosterEmployee6 = new RosterEmployee();
            rosterEmployee4.setEmployee(employee6);
            RosterEmployee rosterEmployee7 = new RosterEmployee();
            rosterEmployee4.setEmployee(employee7);

            onsiteEmployees.add(rosterEmployee4);
            onsiteEmployees.add(rosterEmployee5);
            onsiteEmployees.add(rosterEmployee6);
            onsiteEmployees.add(rosterEmployee7);

            LocalDate now = LocalDate.now();
            LocalDate lastWeek = LocalDate.now().minusDays(7);

            when(employeeService.getAllEmployeesByCompanyIdBeforeDate(any(UUID.class), eq(lastWeek))).thenReturn(new ArrayList<Employee>());
            when(employeeService.getEmployeesOnLeaveByCompanyIdAndDate(any(UUID.class), eq(lastWeek))).thenReturn(new ArrayList<Employee>());
            when(employeeService.getEmployeesByCompanyIdAndDateAndHealthStatus(any(UUID.class),eq(lastWeek), eq(HealthStatus.COVID))).thenReturn(new ArrayList<Employee>());
            when(rosterEmployeeService.findOnsiteRosterEmployeesByCompanyIdAndDate(any(UUID.class), eq(lastWeek))).thenReturn(new ArrayList<>());

            when(employeeService.getAllEmployeesByCompanyId(any(UUID.class))).thenReturn(employeeList);
            when(employeeService.getEmployeesOnLeaveByCompanyIdAndDate(any(UUID.class), eq(now))).thenReturn(employeesOnLeave);
            when(employeeService.getEmployeesByCompanyIdAndDateAndHealthStatus(any(UUID.class),eq(now), eq(HealthStatus.COVID))).thenReturn(employeesWithCovid);
            when(rosterEmployeeService.findOnsiteRosterEmployeesByCompanyIdAndDate(any(UUID.class), eq(now))).thenReturn(onsiteEmployees);

            SummaryResponseModel summaryResponseModel = workStatisticsService.getSummaryByCompanyIdAndDate(companyId, now);

            assertEquals(2, summaryResponseModel.getCovidCount());
            assertEquals(200, summaryResponseModel.getCovidCountChange());

            assertEquals(4, summaryResponseModel.getOnsiteCount());
            assertEquals(400, summaryResponseModel.getOnsiteCountChange());

            assertEquals(1, summaryResponseModel.getLeaveCount());
            assertEquals(100, summaryResponseModel.getLeaveCountChange());

            assertEquals(7, summaryResponseModel.getEmployeesCount());
            assertEquals(700, summaryResponseModel.getEmployeesCountChange());

            verify(employeeService,times(1)).getAllEmployeesByCompanyIdBeforeDate(companyId, lastWeek);
            verify(employeeService,times(1)).getEmployeesOnLeaveByCompanyIdAndDate(companyId, lastWeek);
            verify(employeeService,times(1)).getEmployeesByCompanyIdAndDateAndHealthStatus(companyId, lastWeek, HealthStatus.COVID);
            verify(rosterEmployeeService, times(1)).findOnsiteRosterEmployeesByCompanyIdAndDate(companyId, lastWeek);

            verify(employeeService,times(1)).getAllEmployeesByCompanyId(companyId);
            verify(employeeService,times(1)).getEmployeesOnLeaveByCompanyIdAndDate(companyId, now);
            verify(employeeService,times(1)).getEmployeesByCompanyIdAndDateAndHealthStatus(companyId, now, HealthStatus.COVID);
            verify(rosterEmployeeService, times(1)).findOnsiteRosterEmployeesByCompanyIdAndDate(companyId, now);
        }

        @Test
        void getSummaryByEmployerIdAndDate_returnSummaryOfEmployerCompany() {
            UUID companyId = UUID.randomUUID();
            Company company = new Company();
            company.setId(companyId);

            UUID departmentId = UUID.randomUUID();
            Department department = new Department();
            department.setId(departmentId);
            department.setCompany(company);

            UUID employerId = UUID.randomUUID();
            Employee employer = new Employee();
            employer.setId(employerId);
            employer.setDepartment(department);


            Employee employee1 = new Employee();
            Employee employee2 = new Employee();
            Employee employee3 = new Employee();
            Employee employee4 = new Employee();
            Employee employee5 = new Employee();
            Employee employee6 = new Employee();
            Employee employee7 = new Employee();

            // Mocking the various lists of employees
            List<Employee> employeeList = new ArrayList<>();
            employeeList.add(employee1);
            employeeList.add(employee2);
            employeeList.add(employee3);
            employeeList.add(employee4);
            employeeList.add(employee5);
            employeeList.add(employee6);
            employeeList.add(employee7);

            List<Employee> employeesOnLeave = new ArrayList<>();
            employeesOnLeave.add(employee1);

            List<Employee> employeesWithCovid = new ArrayList<>();
            employeesWithCovid.add(employee2);
            employeesWithCovid.add(employee3);

            List<RosterEmployee> onsiteEmployees = new ArrayList<>();
            RosterEmployee rosterEmployee4 = new RosterEmployee();
            rosterEmployee4.setEmployee(employee4);
            RosterEmployee rosterEmployee5 = new RosterEmployee();
            rosterEmployee4.setEmployee(employee5);
            RosterEmployee rosterEmployee6 = new RosterEmployee();
            rosterEmployee4.setEmployee(employee6);
            RosterEmployee rosterEmployee7 = new RosterEmployee();
            rosterEmployee4.setEmployee(employee7);

            onsiteEmployees.add(rosterEmployee4);
            onsiteEmployees.add(rosterEmployee5);
            onsiteEmployees.add(rosterEmployee6);
            onsiteEmployees.add(rosterEmployee7);

            LocalDate now = LocalDate.now();
            LocalDate lastWeek = LocalDate.now().minusDays(7);

            when(employeeService.getEmployee(any(UUID.class))).thenReturn(employer);

            when(employeeService.getAllEmployeesByCompanyIdBeforeDate(any(UUID.class), eq(lastWeek))).thenReturn(new ArrayList<Employee>());
            when(employeeService.getEmployeesOnLeaveByCompanyIdAndDate(any(UUID.class), eq(lastWeek))).thenReturn(new ArrayList<Employee>());
            when(employeeService.getEmployeesByCompanyIdAndDateAndHealthStatus(any(UUID.class),eq(lastWeek), eq(HealthStatus.COVID))).thenReturn(new ArrayList<Employee>());
            when(rosterEmployeeService.findOnsiteRosterEmployeesByCompanyIdAndDate(any(UUID.class), eq(lastWeek))).thenReturn(new ArrayList<>());

            when(employeeService.getAllEmployeesByCompanyId(any(UUID.class))).thenReturn(employeeList);
            when(employeeService.getEmployeesOnLeaveByCompanyIdAndDate(any(UUID.class), eq(now))).thenReturn(employeesOnLeave);
            when(employeeService.getEmployeesByCompanyIdAndDateAndHealthStatus(any(UUID.class),eq(now), eq(HealthStatus.COVID))).thenReturn(employeesWithCovid);
            when(rosterEmployeeService.findOnsiteRosterEmployeesByCompanyIdAndDate(any(UUID.class), eq(now))).thenReturn(onsiteEmployees);

            SummaryResponseModel summaryResponseModel = workStatisticsService.getSummaryByEmployerIdAndDate(employerId, now);

            assertEquals(2, summaryResponseModel.getCovidCount());
            assertEquals(200, summaryResponseModel.getCovidCountChange());

            assertEquals(4, summaryResponseModel.getOnsiteCount());
            assertEquals(400, summaryResponseModel.getOnsiteCountChange());

            assertEquals(1, summaryResponseModel.getLeaveCount());
            assertEquals(100, summaryResponseModel.getLeaveCountChange());

            assertEquals(7, summaryResponseModel.getEmployeesCount());
            assertEquals(700, summaryResponseModel.getEmployeesCountChange());


            verify(employeeService, times(1)).getEmployee(employerId);

            verify(employeeService,times(1)).getAllEmployeesByCompanyIdBeforeDate(companyId, lastWeek);
            verify(employeeService,times(1)).getEmployeesOnLeaveByCompanyIdAndDate(companyId, lastWeek);
            verify(employeeService,times(1)).getEmployeesByCompanyIdAndDateAndHealthStatus(companyId, lastWeek, HealthStatus.COVID);
            verify(rosterEmployeeService, times(1)).findOnsiteRosterEmployeesByCompanyIdAndDate(companyId, lastWeek);

            verify(employeeService,times(1)).getAllEmployeesByCompanyId(companyId);
            verify(employeeService,times(1)).getEmployeesOnLeaveByCompanyIdAndDate(companyId, now);
            verify(employeeService,times(1)).getEmployeesByCompanyIdAndDateAndHealthStatus(companyId, now, HealthStatus.COVID);
            verify(rosterEmployeeService, times(1)).findOnsiteRosterEmployeesByCompanyIdAndDate(companyId, now);
        }
    }
}

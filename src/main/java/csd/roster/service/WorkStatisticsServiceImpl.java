package csd.roster.service;

import csd.roster.enumerator.HealthStatus;
import csd.roster.model.*;
import csd.roster.response_model.SummaryResponseModel;
import csd.roster.response_model.WorkingStatisticResponseModel;
import csd.roster.service.interfaces.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkStatisticsServiceImpl implements WorkStatisticsService {
    private final CompanyService companyService;
    private final RosterEmployeeService rosterEmployeeService;
    private final EmployeeService employeeService;
    private final EmployeeLogService employeeLogService;

    public WorkStatisticsServiceImpl(CompanyService companyService,
                                     RosterEmployeeService rosterEmployeeService,
                                     EmployeeService employeeService,
                                     EmployeeLogService employeeLogService) {
        this.companyService = companyService;
        this.rosterEmployeeService = rosterEmployeeService;
        this.employeeService = employeeService;
        this.employeeLogService = employeeLogService;
    }

    @Override
    public List<Employee> getOnsiteEmployeesListByCompanyAndDate(final UUID companyId,
                                                                 final LocalDate date) {
        companyService.getCompanyById(companyId);

        List<RosterEmployee> onsiteRosterEmployees = rosterEmployeeService
                .findOnsiteRosterEmployeesByCompanyIdAndDate(companyId, date);

        return onsiteRosterEmployees
                .stream()
                .map(rosterEmployee -> rosterEmployee.getEmployee())
                .collect(Collectors.toList());

    }

    @Override
    public List<WorkingStatisticResponseModel> getWorkStatisticsByCompanyAndDateRange(final UUID companyId,
                                                                                      final LocalDate startDate,
                                                                                      final LocalDate endDate) {
        List<WorkingStatisticResponseModel> statsList = new LinkedList<>();
        for (LocalDate start = startDate; !start.isAfter(endDate); start = start.plusDays(1)) {
            statsList.add(getWorkStatisticsByCompanyAndDate(companyId, start));
        }

        return statsList;
    }

    @Override
    public WorkingStatisticResponseModel getWorkStatisticsByCompanyAndDate(final UUID companyId,
                                                                           final LocalDate date) {
        // Confirm that company exists
        // TODO: use existsById
        companyService.getCompanyById(companyId);

        List<RosterEmployee> currentRosterEmployees = rosterEmployeeService.findAllRosterEmployeesByCompanyIdAndDate(companyId, date);
        int totalWorkingEmployeesCount = currentRosterEmployees.size();

        if (totalWorkingEmployeesCount == 0)
            return new WorkingStatisticResponseModel(companyId, 0, 0);

        List<RosterEmployee> remoteRosterEmployees = rosterEmployeeService.findRemoteRosterEmployeesByCompanyIdAndDate(companyId, date);
        int remoteEmployeesCount = remoteRosterEmployees.size();

        WorkingStatisticResponseModel workingStatisticResponseModel = new WorkingStatisticResponseModel();
        workingStatisticResponseModel.setCompanyId(companyId);
        workingStatisticResponseModel.setRemoteCount(remoteEmployeesCount);
        workingStatisticResponseModel.setOnsiteCount(totalWorkingEmployeesCount - remoteEmployeesCount);

        return workingStatisticResponseModel;
    }

    // TODO: Logic flaw where there might be employees who leave - consider logging them instead
    @Override
    public SummaryResponseModel getSummaryByCompanyIdAndDate(final UUID companyId,
                                                             final LocalDate date) {
        SummaryResponseModel summaryResponseModel = new SummaryResponseModel();

        // Considering whether to use database call to get employees who were created before certain date
        // After some research, consensus seems to be that database query is faster
        // Since EC2 is well integrated with RDS we will just go ahead with querying the database twice
        List<Employee> employees = employeeService.getAllEmployeesByCompanyId(companyId);
        List<Employee> employeesBeforePreviousWeek =
                employeeService.getAllEmployeesByCompanyIdBeforeDate(companyId, date.minusDays(7));

        summaryResponseModel.setEmployeesCount(employees.size());
        summaryResponseModel.setEmployeesCountChange(getChangeRate(employees.size(),
                employeesBeforePreviousWeek.size()));

        List<Employee> employeesOnLeave = employeeService
                .getEmployeesOnLeaveByCompanyIdAndDate(companyId, date);
        List<Employee> employeesOnLeavePreviousWeek = employeeService
                .getEmployeesOnLeaveByCompanyIdAndDate(companyId, date.minusDays(7));

        summaryResponseModel.setLeaveCount(employeesOnLeave.size());
        summaryResponseModel.setLeaveCountChange(getChangeRate(employeesOnLeave.size(),
                employeesOnLeavePreviousWeek.size()));

        List<Employee> employeesOnSite = getOnsiteEmployeesListByCompanyAndDate(companyId, LocalDate.now());
        List<Employee> employeesOnSiteLastWeek = getOnsiteEmployeesListByCompanyAndDate(companyId,
                LocalDate.now().minusDays(7));

        summaryResponseModel.setOnsiteCount(employeesOnSite.size());
        summaryResponseModel.setOnsiteCountChange(getChangeRate(employeesOnSite.size(),
                employeesOnSiteLastWeek.size()));

        List<Employee> employeesWithCovid = employeeService
                .getEmployeesByCompanyIdAndDateAndHealthStatus(companyId, LocalDate.now(), HealthStatus.COVID);
        List<Employee> employeesWithCovidPreviousWeek = employeeService
                .getEmployeesByCompanyIdAndDateAndHealthStatus(companyId, LocalDate.now().minusDays(7), HealthStatus.COVID);

        summaryResponseModel.setCovidCount(employeesWithCovid.size());
        summaryResponseModel.setCovidCountChange(getChangeRate(employeesWithCovid.size(),
                employeesWithCovidPreviousWeek.size()));

        return summaryResponseModel;
    }

    @Override
    public SummaryResponseModel getSummaryByEmployerIdAndDate(final UUID employerId,
                                                              final LocalDate now) {
        Employee employee = employeeService.getEmployee(employerId);

        return getSummaryByCompanyIdAndDate(employee.getDepartment().getCompany().getId(), now);
    }

    private int getChangeRate(int currentValue, int previousValue) {
        int change = currentValue - previousValue;

        if (change == 0) {
            // if company doesn't have any employees change just put 0
            return 0;
        }
        if (currentValue == 0 || previousValue == 0) {
            // Example: if now employee size is 0 and last week was 7, change is -700
            // Example: if last week there's 0 employee and today there's 7, change is +700
            return change * 100;
        } 
        return (int) ((double) change / previousValue * 100);
    }

    private void getEmployeesCountStatistics(final UUID companyId,
                                             final SummaryResponseModel summaryResponseModel,
                                             final LocalDate date) {
        List<Employee> employees = employeeService.getAllEmployeesByCompanyId(companyId);

        // Considering whether to use database call to get employees who were created before certain date
        // After some research, consensus seems to be that database query is faster
        // Since EC2 is well integrated with RDS we will just go ahead with querying the database twice

        List<Employee> employeesBeforePreviousWeek =
                employeeService.getAllEmployeesByCompanyIdBeforeDate(companyId, date.minusDays(7));

        int change = employees.size() - employeesBeforePreviousWeek.size();

        if (employees.size() == 0) {
            if (change == 0) {
                // if company doesn't have any employees change just put 0
                summaryResponseModel.setEmployeesCountChange(change);
            } else {
                // Example: if now employee size is 0 and last week was 7, change is -700
                summaryResponseModel.setEmployeesCountChange(change * 100);
            }
        } else if (employeesBeforePreviousWeek.size() == 0){
            // Example: if last week there's 0 employee and today there's 7, change is +700
            summaryResponseModel.setEmployeesCountChange(change * 100);
        } else {
            int change_rate = (int) ((double) change / employeesBeforePreviousWeek.size() * 100);
            summaryResponseModel.setEmployeesCountChange(change_rate);
        }

        summaryResponseModel.setEmployeesCount(employees.size());
    }

    private void getLeaveCountStatistics(final UUID companyId,
                                         final SummaryResponseModel summaryResponseModel,
                                         final LocalDate date) {
        List<Employee> employeesOnLeave = employeeService
                .getEmployeesOnLeaveByCompanyIdAndDate(companyId, date);

        List<Employee> employeesOnLeavePreviousWeek = employeeService
                .getEmployeesOnLeaveByCompanyIdAndDate(companyId, date.minusDays(7));

        int change = employeesOnLeave.size() - employeesOnLeavePreviousWeek.size();

        if (employeesOnLeave.size() == 0) {
            if (change == 0) {
                summaryResponseModel.setLeaveCountChange(change);
            } else {
                summaryResponseModel.setLeaveCountChange(change * 100);
            }
        } else if (employeesOnLeavePreviousWeek.size() == 0){
            summaryResponseModel.setLeaveCountChange(change * 100);
        } else {
            int change_rate = (int) ((double) change / employeesOnLeavePreviousWeek.size() * 100);
            summaryResponseModel.setLeaveCountChange(change_rate);

        }
        summaryResponseModel.setLeaveCount(employeesOnLeave.size());
    }
}

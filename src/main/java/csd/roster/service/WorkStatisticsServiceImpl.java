package csd.roster.service;

import csd.roster.model.*;
import csd.roster.response_model.SummaryResponseModel;
import csd.roster.response_model.WorkingStatisticResponseModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkStatisticsServiceImpl implements WorkStatisticsService {
    private CompanyService companyService;
    private RosterEmployeeService rosterEmployeeService;
    private EmployeeService employeeService;
    private EmployeeLogService employeeLogService;

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
    public List<Employee> getOnsiteEmployeesListByCompany(UUID companyId) {
        companyService.getCompanyById(companyId);

        List<RosterEmployee> onsiteRosterEmployees = rosterEmployeeService.findOnsiteRosterEmployeesByCompanyIdAndDate(companyId, LocalDate.now());

        return onsiteRosterEmployees
                .stream()
                .map(rosterEmployee -> rosterEmployee.getEmployee())
                .collect(Collectors.toList());

    }

    @Override
    public List<WorkingStatisticResponseModel> getWorkStatisticsByCompanyAndDateRange(UUID companyId, LocalDate startDate, LocalDate endDate) {
        List<WorkingStatisticResponseModel> statsList = new LinkedList<>();
        for (LocalDate start = startDate; !start.isAfter(endDate); start = start.plusDays(1)) {
            statsList.add(getWorkStatisticsByCompanyAndDate(companyId, start));
        }

        return statsList;
    }

    @Override
    public WorkingStatisticResponseModel getWorkStatisticsByCompanyAndDate(UUID companyId, LocalDate date) {
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
    public SummaryResponseModel getSummaryByCompanyIdAndDate(UUID companyId, LocalDate now) {
        SummaryResponseModel summaryResponseModel = new SummaryResponseModel();

        List<Employee> employees = employeeService.getAllEmployeesByCompanyId(companyId);

        // Considering whether to use database call to get employees who were created before certain date
        // After some research, consensus seems to be that database query is faster
        // Since EC2 is well integrated with RDS we will just go ahead with querying the database twice

        List<Employee> employeesBeforePreviousWeek =
                employeeService.getAllEmployeesByCompanyIdBeforeDate(companyId, now.minusDays(7));

        int change = employees.size() - employeesBeforePreviousWeek.size();

        if (employees.size() == 0) {
            summaryResponseModel.setEmployeesCount(employees.size());

            if (change == 0) {
                // if company doesn't have any employees change just put 0
                summaryResponseModel.setEmployeesCountChange(change);
            } else {
                // Example: if now employee size is 0 and last week was 7, change is -700
                summaryResponseModel.setEmployeesCountChange(change * 100);
            }

        } else if (employeesBeforePreviousWeek.size() == 0){
            summaryResponseModel.setEmployeesCount(employees.size());

            // Example: if last week there's 0 employee and today there's 7, change is +700
            summaryResponseModel.setEmployeesCountChange(change * 100);
        } else {
            summaryResponseModel.setEmployeesCount(employees.size());

            int change_rate = (int) ((double) change / employeesBeforePreviousWeek.size() * 100);
        }

        return summaryResponseModel;
    }
}

package csd.roster.service;

import csd.roster.model.*;
import csd.roster.response_model.SummaryResponseModel;
import csd.roster.response_model.WorkingStatisticResponseModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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

    @Override
    public SummaryResponseModel getSummaryByCompanyIdAndDate(UUID companyId, LocalDate now) {
        return null;
    }
}

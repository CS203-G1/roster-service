package csd.roster.service;

import csd.roster.model.Company;
import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;
import csd.roster.model.WorkLocation;
import csd.roster.response_model.WorkingStatisticResponseModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkStatisticsServiceImpl implements WorkStatisticsService {
    private CompanyService companyService;
    private RosterEmployeeService rosterEmployeeService;

    public WorkStatisticsServiceImpl(CompanyService companyService,
                                     RosterEmployeeService rosterEmployeeService) {
        this.companyService = companyService;
        this.rosterEmployeeService = rosterEmployeeService;
    }

    @Override
    public WorkingStatisticResponseModel getCurrentWorkStatisticsByCompany(UUID companyId) {
        // Confirm that company exists
        // TODO: use existsById
        companyService.getCompanyById(companyId);

        List<RosterEmployee> currentRosterEmployees = rosterEmployeeService.findAllRosterEmployeesByCompanyIdAndDate(companyId, LocalDate.now());
        int totalWorkingEmployeesCount = currentRosterEmployees.size();

        if (totalWorkingEmployeesCount == 0)
            return new WorkingStatisticResponseModel(companyId, 0, 0);

        List<RosterEmployee> remoteRosterEmployees = rosterEmployeeService.findRemoteRosterEmployeesByCompanyIdAndDate(companyId, LocalDate.now());
        int remoteEmployeesCount = remoteRosterEmployees.size();

        WorkingStatisticResponseModel workingStatisticResponseModel = new WorkingStatisticResponseModel();
        workingStatisticResponseModel.setCompanyId(companyId);
        workingStatisticResponseModel.setRemoteCount(remoteEmployeesCount);
        workingStatisticResponseModel.setOnsiteCount(totalWorkingEmployeesCount - remoteEmployeesCount);

        return workingStatisticResponseModel;
    }
}

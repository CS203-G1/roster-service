package csd.roster.service;

import csd.roster.model.Company;
import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;
import csd.roster.model.WorkLocation;
import csd.roster.response_model.WorkingStatisticResponseModel;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class WorkStatisticsServiceImpl implements WorkStatisticsService {
    private CompanyService companyService;
    private WorkLocationService workLocationService;
    private RosterService rosterService;

    public WorkStatisticsServiceImpl(CompanyService companyService,
                                     WorkLocationService workLocationService,
                                     RosterService rosterService) {
        this.companyService = companyService;
        this.workLocationService = workLocationService;
        this.rosterService = rosterService;
    }

    @Override
    public WorkingStatisticResponseModel getCurrentWorkStatisticsByCompany(UUID companyId) {
        // Confirm that company exists
        Company company = companyService.getCompanyById(companyId);

        Roster remoteRoster = rosterService.getCurrentRemoteRosterByCompany(companyId);
        Set<RosterEmployee> remoteRosterEmployees = remoteRoster.getRosterEmployees();
        int remoteEmployeesCount = remoteRosterEmployees.size();

        List<Roster> currentRoster = rosterService.getCurrentRostersByCompany(companyId);

        return null;
    }
}

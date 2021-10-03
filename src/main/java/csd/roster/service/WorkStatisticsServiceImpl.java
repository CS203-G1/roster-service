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

        Set<RosterEmployee> remoteRosterEmployees = getCurrentRemoteRosterEmployeesByCompany(companyId);
        int remoteEmployeesCount = remoteRosterEmployees.size();



        return null;
    }

    @Override
    public Set<RosterEmployee> getCurrentRosterEmployeesByCompany(UUID companyId) {
        return null;
    }

    @Override
    public Set<RosterEmployee> getCurrentRemoteRosterEmployeesByCompany(UUID companyId) {

        // Get the remote work location that belongs to this company
        WorkLocation remoteWorkLocation = workLocationService.getRemoteWorkLocationByCompanyId(companyId);

        // Get the roster for today for the remote work location
        Roster remoteRoster = rosterService.getCurrentRosterByWorkLocation(remoteWorkLocation);

        // Get the collection of roster employees that are assigned to this remote roster
        return remoteRoster.getRosterEmployees();
    }
}

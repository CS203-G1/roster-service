package csd.roster.service;

import csd.roster.model.WorkLocation;
import csd.roster.response_model.WorkingStatisticResponseModel;

import java.util.UUID;

public class WorkStatisticsServiceImpl implements WorkStatisticsService {
    private CompanyService companyService;
    private RosterEmployeeService rosterEmployeeService;
    private WorkLocationService workLocationService;

    public WorkStatisticsServiceImpl(CompanyService companyService,
                                     RosterEmployeeService rosterEmployeeService,
                                     WorkLocationService workLocationService) {
        this.companyService = companyService;
        this.rosterEmployeeService = rosterEmployeeService;
        this.workLocationService = workLocationService;
    }

    @Override
    public WorkingStatisticResponseModel getCurrentWorkStatisticsByCompany(UUID companyId) {
        WorkLocation remoteWorkLocation = workLocationService.getRemoteWorkLocationByCompanyId(companyId);

        return null;
    }
}

package csd.roster.service;

import csd.roster.response_model.WorkingStatisticResponseModel;

import java.util.UUID;

public class WorkStatisticsServiceImpl implements WorkStatisticsService {
    private CompanyService companyService;
    private RosterEmployeeService rosterEmployeeService;

    public WorkStatisticsServiceImpl(CompanyService companyService, RosterEmployeeService rosterEmployeeService) {
        this.companyService = companyService;
        this.rosterEmployeeService = rosterEmployeeService;
    }
}

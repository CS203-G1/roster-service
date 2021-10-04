package csd.roster.service;

import csd.roster.model.RosterEmployee;
import csd.roster.response_model.WorkingStatisticResponseModel;

import java.util.Set;
import java.util.UUID;

public interface WorkStatisticsService {
    WorkingStatisticResponseModel getCurrentWorkStatisticsByCompany(UUID companyId);
    Set<RosterEmployee> getCurrentRosterEmployeesByCompany(UUID companyId);
}

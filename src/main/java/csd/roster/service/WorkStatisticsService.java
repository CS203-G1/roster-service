package csd.roster.service;

import csd.roster.model.Employee;
import csd.roster.model.RosterEmployee;
import csd.roster.response_model.WorkingStatisticResponseModel;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface WorkStatisticsService {
    WorkingStatisticResponseModel getCurrentWorkStatisticsByCompany(UUID companyId);

    List<Employee> getCurrentEmployeesListByCompany(UUID companyId);
}

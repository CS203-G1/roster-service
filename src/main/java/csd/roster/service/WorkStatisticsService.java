package csd.roster.service;

import csd.roster.response_model.WorkingStatisticResponseModel;

import java.util.UUID;

public interface WorkStatisticsService {
    WorkingStatisticResponseModel getCurrentWorkStatisticsByCompany(UUID companyId);
}

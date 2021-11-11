package csd.roster.services.service.interfaces;

import csd.roster.domain.model.Employee;
import csd.roster.domain.response_model.SummaryResponseModel;
import csd.roster.domain.response_model.WorkingStatisticResponseModel;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WorkStatisticsService {
    List<Employee> getOnsiteEmployeesListByCompanyAndDate(UUID companyId, LocalDate date);

    List<WorkingStatisticResponseModel> getWorkStatisticsByCompanyAndDateRange(UUID companyId, LocalDate StartDate, LocalDate EndDate);

    WorkingStatisticResponseModel getWorkStatisticsByCompanyAndDate(UUID companyId, LocalDate date);

    SummaryResponseModel getSummaryByCompanyIdAndDate(UUID companyId, LocalDate now);

    SummaryResponseModel getSummaryByEmployerIdAndDate(UUID employerId, LocalDate now);
}

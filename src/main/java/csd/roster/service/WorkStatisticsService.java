package csd.roster.service;

import csd.roster.model.Employee;
import csd.roster.model.RosterEmployee;
import csd.roster.response_model.SummaryResponseModel;
import csd.roster.response_model.WorkingStatisticResponseModel;
import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface WorkStatisticsService {
    List<Employee> getOnsiteEmployeesListByCompany(UUID companyId);

    List<WorkingStatisticResponseModel> getWorkStatisticsByCompanyAndDateRange(UUID companyId, LocalDate StartDate, LocalDate EndDate);

    WorkingStatisticResponseModel getWorkStatisticsByCompanyAndDate(UUID companyId, LocalDate date);

    SummaryResponseModel getSummaryByCompanyIdAndDate(UUID companyId, LocalDate now);
}

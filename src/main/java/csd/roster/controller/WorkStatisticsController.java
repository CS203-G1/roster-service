package csd.roster.controller;

import csd.roster.domain.model.Employee;
import csd.roster.domain.response_model.SummaryResponseModel;
import csd.roster.domain.response_model.WorkingStatisticResponseModel;
import csd.roster.services.service.interfaces.WorkStatisticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ROLE_EMPLOYER')")
public class WorkStatisticsController {
    private final WorkStatisticsService workStatisticsService;


    public WorkStatisticsController(WorkStatisticsService workStatisticsService) {
        this.workStatisticsService = workStatisticsService;
    }

    @GetMapping("/companies/{companyId}/work-statistics/daily")
    public WorkingStatisticResponseModel getDailyWorkStatisticsByCompany(@PathVariable(value = "companyId") final UUID companyId) {
        return workStatisticsService.getWorkStatisticsByCompanyAndDate(companyId, LocalDate.now());
    }

    @GetMapping("/companies/{companyId}/work-statistics/onsite")
    public List<Employee> getOnsiteEmployeesListByCompany(@PathVariable(value = "companyId") final UUID companyId) {
        return workStatisticsService.getOnsiteEmployeesListByCompanyAndDate(companyId, LocalDate.now());
    }

    @GetMapping("/companies/{companyId}/work-statistics/weekly")
    public List<WorkingStatisticResponseModel> getWeeklyWorkStatisticsByCompany(
            @PathVariable(value = "companyId") final UUID companyId) {
        return workStatisticsService.getWorkStatisticsByCompanyAndDateRange(companyId, LocalDate.now().minusDays(6), LocalDate.now());
    }

    @GetMapping("/companies/{companyId}/work-statistics/summary")
    public SummaryResponseModel getDailySummaryByCompany(@PathVariable(value = "companyId") final UUID companyId) {
        return workStatisticsService.getSummaryByCompanyIdAndDate(companyId, LocalDate.now());
    }

    @GetMapping("/employees/{employerId}/work-statistics/summary")
    public SummaryResponseModel getDailySummaryByEmployersCompany(
            @PathVariable(value = "employerId") final UUID employerId) {
        return workStatisticsService.getSummaryByEmployerIdAndDate(employerId, LocalDate.now());
    }
}

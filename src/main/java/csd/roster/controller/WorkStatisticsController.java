package csd.roster.controller;

import csd.roster.exception.ResourceNotFoundException;
import csd.roster.model.Employee;
import csd.roster.model.WorkLocation;
import csd.roster.response_model.SummaryResponseModel;
import csd.roster.response_model.WorkingStatisticResponseModel;
import csd.roster.service.WorkStatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
public class WorkStatisticsController {
    final private WorkStatisticsService workStatisticsService;


    public WorkStatisticsController(WorkStatisticsService workStatisticsService) {
        this.workStatisticsService = workStatisticsService;
    }

    @GetMapping("/companies/{companyId}/work-statistics/daily")
    public WorkingStatisticResponseModel getDailyWorkStatisticsByCompany(@PathVariable(value = "companyId") UUID companyId) {
        return workStatisticsService.getWorkStatisticsByCompanyAndDate(companyId, LocalDate.now());
    }

    @GetMapping("/companies/{companyId}/work-statistics/employees/onsite")
    public List<Employee> getOnsiteEmployeesListByCompany(@PathVariable(value = "companyId") UUID companyId) {
        return workStatisticsService.getOnsiteEmployeesListByCompanyAndDate(companyId, LocalDate.now());
    }

    @GetMapping("/companies/{companyId}/work-statistics/weekly")
    public List<WorkingStatisticResponseModel> getWeeklyWorkStatisticsByCompany(@PathVariable(value = "companyId") UUID companyId) {
        return workStatisticsService.getWorkStatisticsByCompanyAndDateRange(companyId, LocalDate.now().minusDays(6), LocalDate.now());
    }

    @GetMapping("/companies/{companyId}/summary/employees")
    public SummaryResponseModel getDailySummaryByCompany(@PathVariable(value = "companyId") UUID companyId) {
        return workStatisticsService.getSummaryByCompanyIdAndDate(companyId, LocalDate.now());
    }
}

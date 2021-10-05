package csd.roster.controller;

import csd.roster.exception.ResourceNotFoundException;
import csd.roster.model.WorkLocation;
import csd.roster.response_model.WorkingStatisticResponseModel;
import csd.roster.service.WorkStatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class WorkStatisticsController {
    final private WorkStatisticsService workStatisticsService;


    public WorkStatisticsController(WorkStatisticsService workStatisticsService) {
        this.workStatisticsService = workStatisticsService;
    }

    @GetMapping("/companies/{companyId}/work-statistics")
    public WorkingStatisticResponseModel getCurrentWorkStatisticsByCompany(@PathVariable(value = "companyId") UUID companyId) {
        return workStatisticsService.getCurrentWorkStatisticsByCompany(companyId);
    }
}

package csd.roster.controller;

import csd.roster.service.WorkStatisticsService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkStatisticsController {
    final private WorkStatisticsService workStatisticsService;


    public WorkStatisticsController(WorkStatisticsService workStatisticsService) {
        this.workStatisticsService = workStatisticsService;
    }
}

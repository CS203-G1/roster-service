package csd.roster.controller;

import csd.roster.service.interfaces.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class SchedulerController {
    private SchedulerService schedulerService;

    @Autowired
    public SchedulerController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @PostMapping("/schedule/{workLocationId}")
    public Map<Integer, List<UUID>> schedule(@PathVariable(value = "workLocationId") UUID workLocationId) {
        return schedulerService.scheduleRoster(workLocationId);
    }
}

package csd.roster.services.task;

import csd.roster.domain.exception.exceptions.NoOptimalSolutionException;
import csd.roster.model.WorkLocation;
import csd.roster.services.service.interfaces.SchedulerService;
import csd.roster.services.service.interfaces.WorkLocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class ScheduledRosterSchedulingTask {
    private static final Logger log = LoggerFactory.getLogger(ScheduledRosterSchedulingTask.class);
    private final SchedulerService schedulerService;
    private final WorkLocationService workLocationService;

    public ScheduledRosterSchedulingTask(SchedulerService schedulerService, WorkLocationService workLocationService) {
        this.schedulerService = schedulerService;
        this.workLocationService = workLocationService;
    }

    @Scheduled(cron = "0 0 12 * * SUN")
    @Async
    public void scheduleRosters() {
        log.debug("Scheduling roster for all work locations now");
        // Get all work locations
        List<WorkLocation> workLocations = workLocationService.getAllWorkLocations();

        // Loop through all work locations and schedule each one
        for (WorkLocation workLocation : workLocations) {
            try {
                schedulerService.scheduleRoster(workLocation.getId());
            } catch (NoOptimalSolutionException e) {
                log.debug(e.getMessage());
            }
        }
    }
}

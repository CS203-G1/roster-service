package csd.roster.task;

import csd.roster.exception.NoOptimalSolutionException;
import csd.roster.model.Employee;
import csd.roster.model.WorkLocation;
import csd.roster.service.interfaces.SchedulerService;
import csd.roster.service.interfaces.WorkLocationService;
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

    @Scheduled(cron = "0/5 * * * * *")
    @Async
    public void scheduleRosters() {
        // Get all employees
        log.debug("Scheduling roster for all work locations now");
        List<WorkLocation> workLocations = workLocationService.getAllWorkLocations();

        for (WorkLocation workLocation : workLocations) {
            try {
                schedulerService.scheduleRoster(workLocation.getId());
            } catch (NoOptimalSolutionException e) {
                log.debug(e.getMessage());
            }
        }
        // Loop through all employees and log one by one asynchronously
    }
}

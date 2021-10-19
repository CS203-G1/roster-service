package csd.roster.controller;

import csd.roster.model.RosterEmployee;
import csd.roster.service.interfaces.RosterEmployeeService;
import csd.roster.util.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class SchedulerController {
    private Scheduler scheduler;

    @Autowired
    public SchedulerController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostMapping("/schedule")
    public void schedule() {
        scheduler.solve();
    }
}

package csd.roster.controller;

import csd.roster.model.Employee;
import csd.roster.model.RosterEmployee;
import csd.roster.repository.EmployeeRepository;
import csd.roster.service.interfaces.RosterEmployeeService;
import csd.roster.util.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class SchedulerController {
    private Scheduler scheduler;
    private EmployeeRepository employeeRepository;

    @Autowired
    public SchedulerController(Scheduler scheduler, EmployeeRepository employeeRepository) {
        this.scheduler = scheduler;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping("/schedule/{workLocationId}")
    public Map<Integer, List<UUID>> schedule(@PathVariable(value = "workLocationId") UUID workLocationId) {
        List<Employee> li = employeeRepository.findAllByWorkLocationId(workLocationId);
        List<UUID> li2 = li
                .stream()
                .map(e -> e.getId())
                .collect(Collectors.toList());
        Map<Integer, List<UUID>> map = scheduler.solve(li);

        return map;
    }
}

package csd.roster.controller;

import csd.roster.exception.EmployeeNotFoundException;
import csd.roster.exception.WorkLocationNotFoundException;
import csd.roster.model.Employee;
import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;
import csd.roster.model.WorkLocation;
import csd.roster.repository.EmployeeRepository;
import csd.roster.repository.RosterEmployeeRepository;
import csd.roster.repository.RosterRepository;
import csd.roster.repository.WorkLocationRepository;
import csd.roster.util.Scheduler;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static csd.roster.enumerator.HealthStatus.HEALTHY;

@RestController
public class SchedulerController {
    private Scheduler scheduler;
    private EmployeeRepository employeeRepository;
    private RosterRepository rosterRepository;
    private RosterEmployeeRepository rosterEmployeeRepository;
    private WorkLocationRepository workLocationRepository;

    @Autowired
    public SchedulerController(Scheduler scheduler,
                               EmployeeRepository employeeRepository,
                               RosterEmployeeRepository rosterEmployeeRepository,
                               RosterRepository rosterRepository,
                               WorkLocationRepository workLocationRepository) {
        this.scheduler = scheduler;
        this.employeeRepository = employeeRepository;
        this.rosterRepository = rosterRepository;
        this.rosterEmployeeRepository = rosterEmployeeRepository;
        this.workLocationRepository = workLocationRepository;
    }

    @PostMapping("/schedule/{workLocationId}")
    public Map<Integer, List<UUID>> schedule(@PathVariable(value = "workLocationId") UUID workLocationId) {
        List<Employee> li = employeeRepository.findAllByWorkLocationId(workLocationId);
        List<UUID> li2 = li
                .stream()
                .map(e -> e.getId())
                .collect(Collectors.toList());
        Map<Integer, List<UUID>> map = scheduler.solve(li);

        // LocalDate end of week is Sunday
        LocalDate firstDayOfWeek = getFirstDayOfWeek(LocalDate.now()).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusDays(7);

        for (int i = 0; i < 5; i++) {
            LocalDate weekday = firstDayOfWeek.plusDays(i);
            Roster roster = new Roster(null,
                    weekday,
                    workLocationRepository.findById(workLocationId).orElseThrow(() ->
                            new WorkLocationNotFoundException(workLocationId)),
                    weekday.atTime(9, 0),
                    weekday.atTime(17, 0),
                    null);

            rosterRepository.save(roster);

            List<UUID> uuidList = map.get(i);

            for (UUID uuid : uuidList) {
                RosterEmployee rosterEmployee = new RosterEmployee(
                        null,
                        roster,
                        employeeRepository.findById(uuid).orElseThrow(() ->
                                new EmployeeNotFoundException(uuid)),
                        false,
                        HEALTHY
                );

                rosterEmployeeRepository.save(rosterEmployee);
            }
        }

        return map;
    }

    public static Date getFirstDayOfWeek(LocalDate date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(java.sql.Date.valueOf(date));
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
}

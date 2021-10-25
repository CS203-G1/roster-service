package csd.roster.service;

import csd.roster.exception.NoOptimalSolutionException;
import csd.roster.model.Employee;
import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;
import csd.roster.service.interfaces.*;
import csd.roster.util.SchedulerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static csd.roster.enumerator.HealthStatus.HEALTHY;

@Service
public class SchedulerServiceImpl implements SchedulerService {
    // Seems like we need a lot of dependencies
    private SchedulerUtil scheduler;
    private EmployeeService employeeService;
    private RosterService rosterService;
    private RosterEmployeeService rosterEmployeeService;

    @Autowired
    public SchedulerServiceImpl(SchedulerUtil scheduler,
                                EmployeeService employeeService,
                                RosterService rosterService,
                                RosterEmployeeService rosterEmployeeService) {
        this.scheduler = scheduler;
        this.employeeService = employeeService;
        this.rosterService = rosterService;
        this.rosterEmployeeService = rosterEmployeeService;
    }
    @Override
    public Map<Integer, List<UUID>> scheduleRoster(UUID workLocationId) throws NoOptimalSolutionException {
        List<UUID> employeeIdList = getEmployeeIdList(workLocationId);

        Map<Integer, List<UUID>> schedule = scheduler.solve(employeeIdList);

        // LocalDate end of week is Sunday
        LocalDate firstDayOfWeek = getFirstDayOfWeek(LocalDate.now()).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusDays(7);

        scheduleRoster(firstDayOfWeek, schedule, workLocationId);

        return schedule;
    }

    private void scheduleRoster(LocalDate firstDayOfWeek, Map<Integer, List<UUID>> schedule, UUID workLocationId) {
        for (int i = 0; i < 5; i++) {
            LocalDate weekday = firstDayOfWeek.plusDays(i);
            Roster roster = new Roster(
                    null,
                    weekday,
                    null,
                    weekday.atTime(9, 0),
                    weekday.atTime(17, 0),
                    null);

            rosterService.addRoster(workLocationId, roster);

            List<UUID> employeeIds = schedule.get(i);
            List<UUID> allEmployeeIds = employeeService
                    .getAllEmployeesByWorkLocationIdAndHealthStatus(workLocationId, HEALTHY)
                    .stream()
                    .map(e -> e.getId()).collect(Collectors.toList());

            scheduleRosterEmployee(roster, employeeIds);
        }
    }

    private void scheduleRosterEmployee(Roster roster, List<UUID> employeeIds) {
        for (UUID employeeId : employeeIds) {
            RosterEmployee rosterEmployee = new RosterEmployee(
                    null,
                    roster,
                    null,
                    false,
                    HEALTHY
            );

            rosterEmployeeService.addRosterEmployee(roster.getId(), employeeId, rosterEmployee);
        }
    }

    private List<UUID> getEmployeeIdList(UUID workLocationId) {
        List<Employee> employeeList = employeeService
                .getAllEmployeesByWorkLocationIdAndHealthStatus(workLocationId, HEALTHY);
        return employeeList
                .stream()
                .map(e -> e.getId())
                .collect(Collectors.toList());
    }

    private Date getFirstDayOfWeek(LocalDate date) {
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

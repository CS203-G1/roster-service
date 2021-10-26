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
    public Map<Integer, Set<UUID>> scheduleRoster(UUID workLocationId) throws NoOptimalSolutionException {
        List<UUID> employeeIdList = getEmployeeIdList(workLocationId);

        Map<Integer, Set<UUID>> schedule = scheduler.solve(employeeIdList);

        // LocalDate end of week is Sunday
        LocalDate firstDayOfWeek = getFirstDayOfWeek(LocalDate.now()).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusDays(7);

        scheduleRoster(firstDayOfWeek, schedule, workLocationId);

        return schedule;
    }

    private void scheduleRoster(LocalDate firstDayOfWeek, Map<Integer, Set<UUID>> schedule, UUID workLocationId) {
        // After we get the firstDayOfWeek, we iterate Monday through Friday
        for (int i = 0; i < 5; i++) {
            LocalDate weekday = firstDayOfWeek.plusDays(i);

            // Create a roster based on the weekday
            Roster roster = new Roster(
                    null,
                    weekday,
                    null,
                    weekday.atTime(9, 0),
                    weekday.atTime(17, 0),
                    null);

            rosterService.addRoster(workLocationId, roster);

            // Get the employees who are supposed to work on site for the day
            Set<UUID> onsiteEmployeeIds = schedule.get(i);

            // Get all employees who are supposed to work regardless of remote or onsite    
            List<UUID> allEmployeeIds = employeeService
                    .getAllEmployeesByWorkLocationIdAndHealthStatus(workLocationId, HEALTHY)
                    .stream()
                    .map(e -> e.getId()).collect(Collectors.toList());

            scheduleRosterEmployee(roster, onsiteEmployeeIds, allEmployeeIds);
        }
    }

    private void scheduleRosterEmployee(Roster roster, Set<UUID> onsiteEmployeeIds, List<UUID> allEmployeeIds) {
        for (UUID employeeId : allEmployeeIds) {
            RosterEmployee rosterEmployee = new RosterEmployee(
                    null,
                    roster,
                    null,
                    // negate this expression because if onsiteEmployeeIds contains the employeeId
                    // it would be false
                    !onsiteEmployeeIds.contains(employeeId),
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

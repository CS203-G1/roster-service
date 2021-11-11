package csd.roster.services.service;

import csd.roster.domain.exception.exceptions.NoOptimalSolutionException;
import csd.roster.domain.model.Employee;
import csd.roster.domain.model.Roster;
import csd.roster.domain.model.RosterEmployee;
import csd.roster.services.service.interfaces.*;
import csd.roster.repo.util.CalendarUtil;
import csd.roster.repo.util.SchedulerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static csd.roster.domain.enumerator.HealthStatus.HEALTHY;

@Service
public class SchedulerServiceImpl implements SchedulerService {
    // Seems like we need a lot of dependencies
    private final SchedulerUtil scheduler;
    private final EmployeeService employeeService;
    private final RosterService rosterService;
    private final RosterEmployeeService rosterEmployeeService;

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
    public Map<Integer, Set<UUID>> scheduleRoster(final UUID workLocationId) throws NoOptimalSolutionException {
        List<UUID> employeeIdList = getEmployeeIdList(workLocationId);

        Map<Integer, Set<UUID>> schedule = scheduler.solve(employeeIdList);

        // LocalDate end of week is Sunday
        LocalDate firstDayOfWeek = CalendarUtil.getFirstDayOfWeek(LocalDate.now()).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusDays(7);

        scheduleRoster(firstDayOfWeek, schedule, workLocationId);

        return schedule;
    }

    private void scheduleRoster(final LocalDate firstDayOfWeek,
                                final Map<Integer, Set<UUID>> schedule,
                                final UUID workLocationId) {
        // Get all employees who are supposed to work regardless of remote or onsite
        List<UUID> allEmployeeIds = employeeService
                .getAllEmployeesByWorkLocationIdAndHealthStatus(workLocationId, HEALTHY)
                .stream()
                .map(e -> e.getId()).collect(Collectors.toList());

        // After we get the firstDayOfWeek, we iterate Monday through Friday
        for (int i = 0; i < 5; i++) {
            LocalDate weekday = firstDayOfWeek.plusDays(i);

            // Create a roster based on the weekday
            Roster roster = new Roster();

            roster.setDate(weekday);
            roster.setFromDateTime(weekday.atTime(9, 0));
            roster.setToDateTime(weekday.atTime(17, 0));

            rosterService.addRoster(workLocationId, roster);

            // Get the employees who are supposed to work on site for the day
            Set<UUID> onsiteEmployeeIds = schedule.get(i);

            scheduleRosterEmployee(roster, onsiteEmployeeIds, allEmployeeIds);
        }
    }

    private void scheduleRosterEmployee(final Roster roster,
                                        final Set<UUID> onsiteEmployeeIds,
                                        final List<UUID> allEmployeeIds) {
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

    private List<UUID> getEmployeeIdList(final UUID workLocationId) {
        List<Employee> employeeList = employeeService
                .getAllEmployeesByWorkLocationIdAndHealthStatus(workLocationId, HEALTHY);
        return employeeList
                .stream()
                .map(e -> e.getId())
                .collect(Collectors.toList());
    }
}

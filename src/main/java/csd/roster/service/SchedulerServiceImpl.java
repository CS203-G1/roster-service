package csd.roster.service;

import csd.roster.enumerator.HealthStatus;
import csd.roster.model.Employee;
import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;
import csd.roster.service.interfaces.*;
import csd.roster.util.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static csd.roster.enumerator.HealthStatus.HEALTHY;

@Service
public class SchedulerServiceImpl implements SchedulerService {
    private Scheduler scheduler;
    private EmployeeService employeeService;
    private RosterService rosterService;
    private RosterEmployeeService rosterEmployeeService;

    @Autowired
    public SchedulerServiceImpl(Scheduler scheduler,
                                EmployeeService employeeService,
                                RosterService rosterService,
                                RosterEmployeeService rosterEmployeeService) {
        this.scheduler = scheduler;
        this.employeeService = employeeService;
        this.rosterService = rosterService;
        this.rosterEmployeeService = rosterEmployeeService;
    }
    @Override
    public Map<Integer, List<UUID>> scheduleRoster(UUID workLocationId) {
        List<Employee> employeeList = employeeService
                .getAllEmployeesByWorkLocationIdAndHealthStatus(workLocationId, HEALTHY);
        List<UUID> employeeIdList = employeeList
                .stream()
                .map(e -> e.getId())
                .collect(Collectors.toList());
        Map<Integer, List<UUID>> map = scheduler.solve(employeeIdList);

        // LocalDate end of week is Sunday
        LocalDate firstDayOfWeek = getFirstDayOfWeek(LocalDate.now()).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusDays(7);

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

            List<UUID> employeeIds = map.get(i);

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

        return map;
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

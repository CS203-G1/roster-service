package csd.roster.service;

import csd.roster.enumerator.HealthStatus;
import csd.roster.exception.EmployeeNotHealthyException;
import csd.roster.exception.RosterEmployeeNotFoundException;
import csd.roster.model.Employee;
import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;
import csd.roster.repository.RosterEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RosterEmployeeServiceImpl implements RosterEmployeeService {
    private final RosterEmployeeRepository rosterEmployeeRepository;
    private final RosterService rosterService;
    private final EmployeeService employeeService;

    @Autowired
    public RosterEmployeeServiceImpl(RosterEmployeeRepository rosterEmployeeRepository,
                                     RosterService rosterService,
                                     EmployeeService employeeService) {
        this.rosterEmployeeRepository = rosterEmployeeRepository;
        this.rosterService = rosterService;
        this.employeeService = employeeService;
    }

    @Override
    public RosterEmployee addRosterEmployee(UUID rosterId, UUID employeeId, RosterEmployee rosterEmployee) {
        Roster roster = rosterService.getRoster(rosterId);
        Employee employee = employeeService.getEmployee(employeeId);

        rosterEmployee.setEmployee(employee);

        if (employee.getHealthStatus() != HealthStatus.HEALTHY) {
            throw new EmployeeNotHealthyException(employee);
        }

        rosterEmployee.setCurrentHealthStatus(employee.getHealthStatus());
        rosterEmployee.setRoster(roster);

        return rosterEmployeeRepository.save(rosterEmployee);
    }

    @Override
    public RosterEmployee getRosterEmployee(UUID rosterId, UUID employeeId) {
        return rosterEmployeeRepository.findByRosterIdAndEmployeeId(rosterId, employeeId)
                .orElseThrow(() -> new RosterEmployeeNotFoundException(rosterId, employeeId));
    }

    @Override
    public void removeRosterEmployee(UUID rosterId, UUID employeeId) {
        RosterEmployee rosterEmployee = getRosterEmployee(rosterId, employeeId);

        rosterEmployeeRepository.delete(rosterEmployee);
    }

    @Override
    public RosterEmployee updateRosterEmployee(UUID rosterId, UUID employeeId, RosterEmployee newRosterEmployee) {
        RosterEmployee rosterEmployee = getRosterEmployee(rosterId, employeeId);

        // Different from the usual updates because we only want to allow the frontend request to be able to
        // update from date time and to date time
        rosterEmployee.setFromDateTime(newRosterEmployee.getFromDateTime());
        rosterEmployee.setToDateTime(newRosterEmployee.getToDateTime());

        return rosterEmployeeRepository.save(rosterEmployee);
    }
}

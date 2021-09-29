package csd.roster.service;

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
        rosterEmployee.setCurrentHealthStatus(employee.getHealthStatus());
        rosterEmployee.setRoster(roster);

        return rosterEmployeeRepository.save(rosterEmployee);
    }
}

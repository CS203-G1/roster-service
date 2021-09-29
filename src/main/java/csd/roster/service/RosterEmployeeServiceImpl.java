package csd.roster.service;

import csd.roster.model.Employee;
import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;
import csd.roster.repository.RosterEmployeeRepository;

import java.util.UUID;

public class RosterEmployeeServiceImpl implements RosterEmployeeService {
    private final RosterEmployeeRepository rosterEmployeeRepository;
    private final RosterService rosterService;
    private final EmployeeService employeeService;

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
        rosterEmployee.setRoster(roster);



        return null;
    }
}

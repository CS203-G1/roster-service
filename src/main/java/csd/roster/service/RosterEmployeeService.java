package csd.roster.service;

import csd.roster.model.RosterEmployee;

import java.util.UUID;

public interface RosterEmployeeService {
    RosterEmployee addRosterEmployee(UUID rosterId, UUID employeeId, RosterEmployee rosterEmployee);

}

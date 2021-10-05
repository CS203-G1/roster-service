package csd.roster.service;

import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RosterEmployeeService {
    RosterEmployee addRosterEmployee(UUID rosterId, UUID employeeId, RosterEmployee rosterEmployee);
    RosterEmployee getRosterEmployee(UUID rosterId, UUID employeeId);
    void removeRosterEmployee(UUID rosterId, UUID employeeId);
    RosterEmployee updateRosterEmployee(UUID rosterId, UUID employeeId, RosterEmployee rosterEmployee);
    List<RosterEmployee> findAllRosterEmployeesByCompanyIdAndDate(UUID companyId, LocalDate date);
    List<RosterEmployee> findRemoteRosterEmployeesByCompanyIdAndDate(UUID companyId, LocalDate date);
}

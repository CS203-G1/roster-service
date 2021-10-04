package csd.roster.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;
import csd.roster.model.WorkLocation;

public interface RosterService {
    Roster addRoster(UUID workLocationId, Roster roster);

    List<Roster> getRosters(UUID workLocationId);

    Roster getRoster(UUID workLocationId, UUID rosterId);

    Roster getRoster(UUID rosterId);

    void deleteRoster(UUID workLocationId, UUID rosterId);

    Roster updateRoster(UUID workLocationId, UUID rosterId, Roster roster);

    Roster getCurrentRosterByWorkLocation(WorkLocation workLocation);

    Set<RosterEmployee> getCurrentRemoteRosterEmployeesByCompany(UUID companyId);
}
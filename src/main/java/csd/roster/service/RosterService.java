package csd.roster.service;

import java.util.List;
import java.util.UUID;

import csd.roster.model.Roster;

public interface RosterService {
    Roster addRoster(UUID workLocationId, Roster roster);
    List<Roster> getRosters(UUID workLocationId);
    Roster getRoster(UUID workLocationId, UUID rosterId);
    void deleteRoster(UUID workLocationId, UUID rosterId);
    Roster updateRoster(UUID workLocationId, UUID rosterId, Roster roster);
}
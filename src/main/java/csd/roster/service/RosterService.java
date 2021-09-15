package csd.roster.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import csd.roster.model.Roster;

public interface RosterService {
    Roster addRoster(UUID companyId, UUID departmentId, UUID workLocationId, Roster roster);
    List<Roster> getRosters(UUID companyId, UUID departmentId, UUID workLocationId);
    Roster getRoster(UUID companyId, UUID departmentId, UUID workLocationId, UUID rosterId);
    void delete(UUID companyId, UUID departmentId, UUID workLocationId, UUID rosterId);
    Roster update(UUID companyId, UUID departmentId, UUID workLocationId, UUID rosterId, LocalDate date);
}
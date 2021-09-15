package csd.roster.service;

import csd.roster.model.Roster;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RosterService {
    Roster getRosterById(UUID rosterId);
    Roster getRosterByIdAndWorkLocationId(UUID rosterId, UUID workLocationId);
    List<Roster> getRostersByWorkLocationId(UUID workLocationId);
    void delete(UUID rosterId, UUID workLocationId);
    Roster update(UUID rosterId, UUID workLocationId, LocalDate date);
}

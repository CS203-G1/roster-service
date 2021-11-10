package csd.roster.services.service.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import csd.roster.model.Roster;
import csd.roster.model.WorkLocation;
import csd.roster.response_model.RosterResponseModel;

public interface RosterService {
    Roster addRoster(UUID workLocationId, Roster roster);

    List<Roster> getRosters(UUID workLocationId);

    Roster getRoster(UUID workLocationId, UUID rosterId);

    Roster getRoster(UUID rosterId);

    void deleteRoster(UUID workLocationId, UUID rosterId);

    Roster updateRoster(UUID workLocationId, UUID rosterId, Roster roster);

    Roster getCurrentRosterByWorkLocation(WorkLocation workLocation);

    Roster getCurrentRemoteRosterByCompany(UUID companyId);

    List<Roster> getCurrentRostersByCompany(UUID companyId);

    List<RosterResponseModel>  getRostersByEmployerIdAndDate(UUID employerId, LocalDate date);

    List<Roster> getWeeklyRostersByEmployeeId(UUID employeeId);
}
package csd.roster.services.service.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import csd.roster.domain.model.Roster;
import csd.roster.domain.model.WorkLocation;
import csd.roster.domain.response_model.RosterResponseModel;

public interface RosterService {
    Roster addRoster(UUID workLocationId, Roster roster);

    List<Roster> getRostersByWorkLocationId(UUID workLocationId);

    Roster getRosterByIdAndWorkLocationId(UUID workLocationId, UUID rosterId);

    Roster getRosterById(UUID rosterId);

    void deleteRosterByIdAndWorkLocationId(UUID workLocationId, UUID rosterId);

    Roster updateRosterByIdAndWorkLocationId(UUID workLocationId, UUID rosterId, Roster roster);

    Roster getCurrentRosterByWorkLocation(WorkLocation workLocation);

    Roster getCurrentRemoteRosterByCompany(UUID companyId);

    List<Roster> getCurrentRostersByCompany(UUID companyId);

    List<RosterResponseModel>  getRostersByEmployerIdAndDate(UUID employerId, LocalDate date);

    List<Roster> getWeeklyRostersByEmployeeId(UUID employeeId);
}
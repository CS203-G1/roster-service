package csd.roster.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import csd.roster.exception.RosterNotFoundException;
import csd.roster.model.Roster;
import csd.roster.model.WorkLocation;
import csd.roster.repository.RosterRepository;

@Service
public class RosterServiceImpl implements RosterService {
    private RosterRepository rosterRepository;
    private WorkLocationService workLocationService;

    @Autowired
    public RosterServiceImpl(RosterRepository rosterRepository, WorkLocationService workLocationService) {
        this.rosterRepository = rosterRepository;
        this.workLocationService = workLocationService;
    }

    @Override
    public Roster addRoster(UUID workLocationId, Roster roster) {
        WorkLocation workLocation = workLocationService.getWorkLocationById(workLocationId);
        roster.setWorkLocation(workLocation);

        return rosterRepository.save(roster);
    }

    @Override
    public List<Roster> getRosters(UUID workLocationId) {
        List<Roster> rosters = rosterRepository.findByWorkLocationId(workLocationId);

        if (rosters.isEmpty()) {
            throw new RosterNotFoundException(workLocationId.toString());
        }
        return rosters;
    }

    @Override
    public Roster getRoster(UUID workLocationId, UUID rosterId) {
        return rosterRepository.findByIdAndWorkLocationId(rosterId, workLocationId)
                .orElseThrow(() -> new RosterNotFoundException(rosterId, workLocationId));
    }

    // This is added and meant to be used in RosterEmployeeService
    @Override
    public Roster getRoster(UUID rosterId) {
        return rosterRepository.findById(rosterId)
                .orElseThrow(() -> new RosterNotFoundException(rosterId));
    }

    @Override
    public void deleteRoster(UUID workLocationId, UUID rosterId) {
        Roster roster = getRoster(workLocationId, rosterId);

        rosterRepository.delete(roster);
    }

    @Override
    public Roster updateRoster(UUID workLocationId, UUID rosterId, Roster roster) {
        return rosterRepository.findByIdAndWorkLocationId(rosterId, workLocationId).map(oldRoster -> {
            oldRoster.setDate(roster.getDate());
            return rosterRepository.save(oldRoster);

        }).orElseThrow(() -> new RosterNotFoundException(rosterId, workLocationId));
    }

    @Override
    public Roster getCurrentRosterByWorkLocation(WorkLocation workLocation) {
        return rosterRepository.findByWorkLocationIdAndDate(workLocation.getId(), LocalDate.now())
                .orElseThrow(() -> new RosterNotFoundException(workLocation));
    }
}
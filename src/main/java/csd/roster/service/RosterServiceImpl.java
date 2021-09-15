package csd.roster.service;

import java.time.LocalDate;
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
    public Roster addRoster(UUID companyId, UUID departmentId, UUID workLocationId, Roster roster) {
        WorkLocation workLocation = workLocationService.get(companyId, departmentId, workLocationId);
        roster.setWorkLocation(workLocation);

        return rosterRepository.save(roster);
    }

    @Override
    public List<Roster> getRosters(UUID companyId, UUID departmentId, UUID workLocationId) {
        workLocationService.get(companyId, departmentId, workLocationId);

        return rosterRepository.findByWorkLocationId(workLocationId);
    }

    @Override
    public Roster getRoster(UUID companyId, UUID departmentId, UUID workLocationId, UUID rosterId) {
        workLocationService.get(companyId, departmentId, workLocationId);

        return rosterRepository.findByIdAndWorkLocationId(rosterId, workLocationId)
                .orElseThrow(() -> new RosterNotFoundException(rosterId, workLocationId, departmentId, companyId));
    }

    
}
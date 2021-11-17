package csd.roster.services.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import csd.roster.domain.model.Employee;
import csd.roster.domain.response_model.RosterResponseModel;
import csd.roster.services.service.interfaces.EmployeeService;
import csd.roster.services.service.interfaces.RosterService;
import csd.roster.services.service.interfaces.WorkLocationService;
import csd.roster.repo.util.CalendarUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import csd.roster.domain.exception.exceptions.RosterNotFoundException;
import csd.roster.domain.model.Roster;
import csd.roster.domain.model.WorkLocation;
import csd.roster.repo.repository.RosterRepository;

@Service
public class RosterServiceImpl implements RosterService {
    private final RosterRepository rosterRepository;
    private final WorkLocationService workLocationService;
    private final EmployeeService employeeService;

    @Autowired
    public RosterServiceImpl(RosterRepository rosterRepository,
                             WorkLocationService workLocationService,
                             EmployeeService employeeService) {
        this.rosterRepository = rosterRepository;
        this.workLocationService = workLocationService;
        this.employeeService = employeeService;
    }

    @Override
    public Roster addRoster(final UUID workLocationId, final Roster roster) {
        WorkLocation workLocation = workLocationService.getWorkLocationById(workLocationId);
        roster.setWorkLocation(workLocation);

        return rosterRepository.save(roster);
    }

    @Override
    public List<Roster> getRostersByWorkLocationId(final UUID workLocationId) {
        List<Roster> rosters = rosterRepository.findByWorkLocationId(workLocationId);

        if (rosters.isEmpty()) {
            throw new RosterNotFoundException(workLocationId.toString());
        }
        return rosters;
    }

    @Override
    public Roster getRosterByIdAndWorkLocationId(final UUID workLocationId, final UUID rosterId) {
        workLocationService.getWorkLocationById(workLocationId);
        return rosterRepository.findByIdAndWorkLocationId(rosterId, workLocationId)
                .orElseThrow(() -> new RosterNotFoundException(rosterId, workLocationId));
    }

    // This is added and meant to be used in RosterEmployeeService
    @Override
    public Roster getRosterById(final UUID rosterId) {
        return rosterRepository.findById(rosterId)
                .orElseThrow(() -> new RosterNotFoundException(rosterId));
    }

    @Override
    public void deleteRosterByIdAndWorkLocationId(final UUID workLocationId, final UUID rosterId) {
        Roster roster = getRosterByIdAndWorkLocationId(workLocationId, rosterId);

        rosterRepository.delete(roster);
    }

    @Override
    public Roster updateRosterByIdAndWorkLocationId(final UUID workLocationId, final UUID rosterId, final Roster roster) {
        workLocationService.getWorkLocationById(workLocationId);
        return rosterRepository.findByIdAndWorkLocationId(rosterId, workLocationId).map(oldRoster -> {
            oldRoster.setFromDateTime(roster.getFromDateTime());
            oldRoster.setToDateTime(roster.getToDateTime());
            return rosterRepository.save(oldRoster);

        }).orElseThrow(() -> new RosterNotFoundException(rosterId, workLocationId));
    }

    @Override
    public Roster getCurrentRosterByWorkLocation(final WorkLocation workLocation) {
        return rosterRepository.findByWorkLocationIdAndDate(workLocation.getId(), LocalDate.now())
                .orElseThrow(() -> new RosterNotFoundException(workLocation));
    }

    @Override
    public Roster getCurrentRemoteRosterByCompany(final UUID companyId) {

        // Get the remote work location that belongs to this company
        WorkLocation remoteWorkLocation = workLocationService.getRemoteWorkLocationByCompanyId(companyId);

        // Get the roster for today for the remote work location
        return getCurrentRosterByWorkLocation(remoteWorkLocation);
    }

    @Override
    public List<Roster> getCurrentRostersByCompany(final UUID companyId) {
        List<WorkLocation> workLocations = workLocationService.getWorkLocationsByCompanyId(companyId);

        // Using linked list data structure to have an O(1) of appending the list
        List<Roster> rosters = new LinkedList<Roster>();

        for (WorkLocation workLocation : workLocations) {
            rosters.add(getCurrentRosterByWorkLocation(workLocation));
        }

        return rosters;
    }

    @Override
    public List<RosterResponseModel> getRostersByEmployerIdAndDate(final UUID employerId, final LocalDate date) {
        Employee employer = employeeService.getEmployee(employerId);

        List<WorkLocation> workLocations = workLocationService
                .getWorkLocationsByCompanyId(employer.getCompany().getId());

        List<Roster> rosters = new LinkedList<Roster>();

        for (WorkLocation workLocation : workLocations) {
            rosters.add(rosterRepository.findByWorkLocationIdAndDate(workLocation.getId(), date)
                    .orElseThrow(() -> new RosterNotFoundException(workLocation)));
        }

        List<RosterResponseModel> rosterResponseModels = new LinkedList<>();
        for (Roster roster : rosters) {
            RosterResponseModel rosterResponseModel = new RosterResponseModel(roster, null);

            rosterResponseModel.setEmployees(rosterRepository.findOnsiteEmployeesByRosterId(roster.getId()));

            rosterResponseModels.add(rosterResponseModel);
        }

        return rosterResponseModels;
    }

    @Override
    public List<Roster> getWeeklyRostersByEmployeeId(final UUID employeeId) {
        Employee employer = employeeService.getEmployee(employeeId);

        LocalDate firstDayOfWeek = CalendarUtil.getFirstDayOfWeek(LocalDate.now()).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        List<Roster> rosters = new LinkedList<Roster>();

        for (int i = 0; i < 5; i++) {
            LocalDate weekday = firstDayOfWeek.plusDays(i);

            List<Roster> roster = rosterRepository.findByEmployeeIdAndDate(employeeId, weekday);

            rosters.add(roster.isEmpty() ? null : roster.get(0));
        }

        return rosters;
    }
}
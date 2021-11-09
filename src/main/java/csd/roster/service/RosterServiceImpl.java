package csd.roster.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import csd.roster.model.Employee;
import csd.roster.response_model.RosterResponseModel;
import csd.roster.service.interfaces.EmployeeService;
import csd.roster.service.interfaces.RosterService;
import csd.roster.service.interfaces.WorkLocationService;
import csd.roster.util.CalendarUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import csd.roster.exception.exceptions.RosterNotFoundException;
import csd.roster.model.Roster;
import csd.roster.model.WorkLocation;
import csd.roster.repository.RosterRepository;

@Service
public class RosterServiceImpl implements RosterService {
    private RosterRepository rosterRepository;
    private WorkLocationService workLocationService;
    private EmployeeService employeeService;

    @Autowired
    public RosterServiceImpl(RosterRepository rosterRepository,
                             WorkLocationService workLocationService,
                             EmployeeService employeeService) {
        this.rosterRepository = rosterRepository;
        this.workLocationService = workLocationService;
        this.employeeService = employeeService;
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
            oldRoster.setFromDateTime(roster.getFromDateTime());
            oldRoster.setToDateTime(roster.getToDateTime());
            return rosterRepository.save(oldRoster);

        }).orElseThrow(() -> new RosterNotFoundException(rosterId, workLocationId));
    }

    @Override
    public Roster getCurrentRosterByWorkLocation(WorkLocation workLocation) {
        return rosterRepository.findByWorkLocationIdAndDate(workLocation.getId(), LocalDate.now())
                .orElseThrow(() -> new RosterNotFoundException(workLocation));
    }

    @Override
    public Roster getCurrentRemoteRosterByCompany(UUID companyId) {

        // Get the remote work location that belongs to this company
        WorkLocation remoteWorkLocation = workLocationService.getRemoteWorkLocationByCompanyId(companyId);

        // Get the roster for today for the remote work location
        return getCurrentRosterByWorkLocation(remoteWorkLocation);
    }

    @Override
    public List<Roster> getCurrentRostersByCompany(UUID companyId) {
        List<WorkLocation> workLocations = workLocationService.getWorkLocationsByCompanyId(companyId);

        // Using linked list data structure to have an O(1) of appending the list
        List<Roster> rosters = new LinkedList<Roster>();

        for (WorkLocation workLocation : workLocations) {
            rosters.add(getCurrentRosterByWorkLocation(workLocation));
        }

        return rosters;
    }

    @Override
    public List<RosterResponseModel> getRostersByEmployerIdAndDate(UUID employerId, LocalDate date) {
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

            rosterResponseModel.setEmployees(rosterRepository.findEmployeesByRosterId(roster.getId()));

            rosterResponseModels.add(rosterResponseModel);
        }

        return rosterResponseModels;
    }

    @Override
    public List<Roster> getWeeklyRostersByEmployeeId(UUID employeeId) {
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
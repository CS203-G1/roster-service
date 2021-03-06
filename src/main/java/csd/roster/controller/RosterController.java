package csd.roster.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import csd.roster.domain.response_model.RosterResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import csd.roster.domain.model.Roster;
import csd.roster.services.service.interfaces.RosterService;

@RestController
@PreAuthorize("hasRole('ROLE_EMPLOYER')")
public class RosterController {
    private final RosterService rosterService;

    @Autowired
    public RosterController(RosterService rosterService) {
        this.rosterService = rosterService;
    }

    @GetMapping("/work-locations/{workLocationId}/rosters/{rosterId}")
    public Roster getRoster(@PathVariable(value = "workLocationId") final UUID workLocationId,
                            @PathVariable(value = "rosterId") final UUID rosterId) {
        return rosterService.getRosterByIdAndWorkLocationId(workLocationId, rosterId);
    }

    @GetMapping("/work-locations/{workLocationId}/rosters")
    public List<Roster> getRosters(@PathVariable(value = "workLocationId") final UUID workLocationId) {
        return rosterService.getRostersByWorkLocationId(workLocationId);
    }

    @PostMapping("/work-locations/{workLocationId}/rosters")
    public Roster addRoster(@PathVariable(value = "workLocationId") final UUID workLocationId,
                            @Valid @RequestBody final Roster roster) {
        return rosterService.addRoster(workLocationId, roster);
    }

    @DeleteMapping("/work-locations/{workLocationId}/rosters/{rosterId}")
    public void deleteRoster(@PathVariable(value = "workLocationId") final UUID workLocationId,
                            @PathVariable(value = "rosterId") final UUID rosterId) {
        rosterService.deleteRosterByIdAndWorkLocationId(workLocationId, rosterId);
    }

    @PutMapping("/work-locations/{workLocationId}/rosters/{rosterId}")
    public Roster updateRoster(@PathVariable(value = "workLocationId") final UUID workLocationId,
                            @PathVariable(value = "rosterId") final UUID rosterId,
                            @Valid @RequestBody final Roster roster) {
        return rosterService.updateRosterByIdAndWorkLocationId(workLocationId, rosterId, roster);
    }

    @GetMapping("/employers/{employerId}/rosters/date/{date}")
    public List<RosterResponseModel> getRostersByEmployerIdAndDate(@PathVariable(value = "employerId") final UUID employerId,
                                                                   @PathVariable(value = "date")
                                                                   @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate date) {
        return rosterService.getRostersByEmployerIdAndDate(employerId, date);
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/employees/{employeeId}/rosters/weekly")
    public List<Roster> getWeeklyRostersByEmployeeId(@PathVariable(value = "employeeId") final UUID employeeId) {
        return rosterService.getWeeklyRostersByEmployeeId(employeeId);
    }
}
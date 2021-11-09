package csd.roster.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import csd.roster.response_model.RosterResponseModel;
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

import csd.roster.model.Roster;
import csd.roster.service.interfaces.RosterService;

@RestController
@PreAuthorize("hasRole('ROLE_EMPLOYER')")
public class RosterController {
    private RosterService rosterService;

    @Autowired
    public RosterController(RosterService rosterService) {
        this.rosterService = rosterService;
    }

    @GetMapping("/work-locations/{workLocationId}/rosters/{rosterId}")
    public Roster getRoster(@PathVariable(value = "workLocationId") UUID workLocationId,
                            @PathVariable(value = "rosterId") UUID rosterId) {
        return rosterService.getRoster(workLocationId, rosterId);
    }

    @GetMapping("/work-locations/{workLocationId}/rosters")
    public List<Roster> getRosters(@PathVariable(value = "workLocationId") UUID workLocationId) {
        return rosterService.getRosters(workLocationId);
    }

    @PostMapping("/work-locations/{workLocationId}/rosters")
    public Roster addRoster(@PathVariable(value = "workLocationId") UUID workLocationId,
                            @Valid @RequestBody Roster roster) {
        return rosterService.addRoster(workLocationId, roster);
    }

    @DeleteMapping("/work-locations/{workLocationId}/rosters/{rosterId}")
    public void deleteRoster(@PathVariable(value = "workLocationId") UUID workLocationId,
                            @PathVariable(value = "rosterId") UUID rosterId) {
        rosterService.deleteRoster(workLocationId, rosterId);
    }

    @PutMapping("/work-locations/{workLocationId}/rosters/{rosterId}")
    public Roster updateRoster(@PathVariable(value = "workLocationId") UUID workLocationId,
                            @PathVariable(value = "rosterId") UUID rosterId,
                            @Valid @RequestBody Roster roster) {
        return rosterService.updateRoster(workLocationId, rosterId, roster);
    }

    @GetMapping("/employers/{employerId}/rosters/date/{date}")
    public List<RosterResponseModel> getRostersByEmployerIdAndDate(@PathVariable(value = "employerId") UUID employerId,
                                                                   @PathVariable(value = "date")  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return rosterService.getRostersByEmployerIdAndDate(employerId, date);
    }

    @GetMapping("/employee/{employeeId}/rosters/weekly")
    public List<Roster> getWeeklyRostersByEmployeeId(@PathVariable(value = "employeeId")UUID employeeId) {
        return rosterService.getWeeklyRostersByEmployeeId(employeeId);
    }
}
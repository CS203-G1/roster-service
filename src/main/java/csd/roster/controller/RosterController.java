package csd.roster.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import csd.roster.model.Roster;
import csd.roster.service.RosterService;

@RestController
public class RosterController {
    private RosterService rosterService;

    @Autowired
    public RosterController(RosterService rosterService) {
        this.rosterService = rosterService;
    }

    @GetMapping("/companies/{companyId}/departments/{departmentId}/work-locations/{workLocationId}/rosters/{rosterId}")
    public Roster getRoster(@PathVariable(value = "companyId") UUID companyId,
            @PathVariable(value = "departmentId") UUID departmentId,
            @PathVariable(value = "workLocationId") UUID workLocationId,
            @PathVariable(value = "rosterId") UUID rosterId) {
        return rosterService.getRoster(companyId, departmentId, workLocationId, rosterId);
    }

    @GetMapping("/companies/{companyId}/departments/{departmentId}/work-locations/{workLocationId}/rosters")
    public List<Roster> getRosters(@PathVariable(value = "companyId") UUID companyId,
            @PathVariable(value = "departmentId") UUID departmentId,
            @PathVariable(value = "workLocationId") UUID workLocationId) {
        return rosterService.getRosters(companyId, departmentId, workLocationId);
    }

    @PostMapping("/companies/{companyId}/departments/{departmentId}/work-locations/{workLocationId}/rosters")
    public Roster addRoster(@PathVariable(value = "companyId") UUID companyId,
            @PathVariable(value = "departmentId") UUID departmentId,
            @PathVariable(value = "workLocationId") UUID workLocationId,
            @Valid @RequestBody Roster roster) {
        return rosterService.addRoster(companyId, departmentId, workLocationId, roster);
    }
}

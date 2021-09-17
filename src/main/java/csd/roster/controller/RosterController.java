package csd.roster.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import csd.roster.exception.RosterNotFoundException;
import csd.roster.model.Roster;
import csd.roster.service.RosterService;

@RestController
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
        try {
            rosterService.deleteRoster(workLocationId, rosterId);
        } catch (EmptyResultDataAccessException e) {
            throw new RosterNotFoundException(rosterId, workLocationId);
        }
    }

    @PutMapping("/work-locations/{workLocationId}/rosters/{rosterId}")
    public Roster updateRoster(@PathVariable(value = "workLocationId") UUID workLocationId,
                            @PathVariable(value = "rosterId") UUID rosterId,
                            @RequestBody Roster roster) {
        return rosterService.updateRoster(workLocationId, rosterId, roster);
    }
}
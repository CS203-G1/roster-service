package csd.roster.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import csd.roster.model.Roster;
import csd.roster.service.RosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import csd.roster.model.RosterEmployee;
import csd.roster.service.RosterEmployeeService;

@RestController
public class RosterEmployeeController {
    private final RosterEmployeeService rosterEmployeeService;

    @Autowired
    public RosterEmployeeController(RosterEmployeeService rosterEmployeeService) {
        this.rosterEmployeeService = rosterEmployeeService;
    }

    @PostMapping("/rosters/{rosterId}/employees/{employeeId}")
    public RosterEmployee addRosterEmployee(@PathVariable(value = "rosterId") UUID rosterId,
                            @PathVariable(value = "employeeId") UUID employeeId,
                            @Valid @RequestBody RosterEmployee rosterEmployee) {
        return rosterEmployeeService.addRosterEmployee(rosterId, employeeId, rosterEmployee);
    }
}

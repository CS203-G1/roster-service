package csd.roster.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import csd.roster.model.RosterEmployee;
import csd.roster.service.interfaces.RosterEmployeeService;

@RestController
@PreAuthorize("hasRole('ROLE_EMPLOYER')")
public class RosterEmployeeController {
    private final RosterEmployeeService rosterEmployeeService;

    @Autowired
    public RosterEmployeeController(RosterEmployeeService rosterEmployeeService) {
        this.rosterEmployeeService = rosterEmployeeService;
    }

    // Note that you can actually define this on the controller layer but viewing RosterEmployee is allowed for
    // employees
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    @PostMapping("/rosters/{rosterId}/employees/{employeeId}")
    public RosterEmployee addRosterEmployee(@PathVariable(value = "rosterId") final UUID rosterId,
                            @PathVariable(value = "employeeId") final UUID employeeId,
                            @Valid @RequestBody RosterEmployee rosterEmployee) {
        return rosterEmployeeService.addRosterEmployee(rosterId, employeeId, rosterEmployee);
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    @DeleteMapping("/rosters/{rosterId}/employees/{employeeId}")
    public void deleteRosterEmployee(@PathVariable(value = "rosterId") final UUID rosterId,
                                            @PathVariable(value = "employeeId") final UUID employeeId) {
        rosterEmployeeService.removeRosterEmployee(rosterId, employeeId);
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    @PutMapping("/rosters/{rosterId}/employees/{employeeId}")
    public RosterEmployee updateRosterEmployee(@PathVariable(value = "rosterId") final UUID rosterId,
                                            @PathVariable(value = "employeeId") final UUID employeeId,
                                            @Valid @RequestBody final RosterEmployee rosterEmployee) {
        return rosterEmployeeService.updateRosterEmployee(rosterId, employeeId, rosterEmployee);
    }
}

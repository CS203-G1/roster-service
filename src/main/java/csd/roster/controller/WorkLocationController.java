package csd.roster.controller;

import csd.roster.domain.exception.exceptions.ResourceNotFoundException;
import csd.roster.domain.model.WorkLocation;
import csd.roster.services.service.interfaces.WorkLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ROLE_EMPLOYER')")
public class WorkLocationController {
    private final WorkLocationService workLocationService;

    @Autowired
    public WorkLocationController(WorkLocationService workLocationService) {
        this.workLocationService = workLocationService;
    }

    @PostMapping("/departments/{departmentId}/work-locations")
    public WorkLocation addWorkLocation(@PathVariable (value = "departmentId") final UUID departmentId,
                                           @RequestBody final WorkLocation workLocation) {
        return workLocationService.add(departmentId, workLocation);
    }

    @GetMapping("/departments/{departmentId}/work-locations/{workLocationId}")
    public WorkLocation getWorkLocation(@PathVariable (value = "departmentId") final UUID departmentId,
                                           @PathVariable (value = "workLocationId") final UUID workLocationId) {
        return workLocationService.get(departmentId, workLocationId);
    }

    @GetMapping("/work-locations/{workLocationId}")
    public WorkLocation getWorkLocation(@PathVariable (value = "workLocationId") final UUID workLocationId) {
        return workLocationService.getWorkLocationById(workLocationId);
    }

    @PutMapping("/departments/{departmentId}/work-locations/{workLocationId}")
    public WorkLocation updateWorkLocation(@PathVariable (value = "departmentId") final UUID departmentId,
                                             @PathVariable (value = "workLocationId") final UUID workLocationId,
                                                @RequestBody final WorkLocation newWorkLocation) {
        return workLocationService.update(departmentId, workLocationId, newWorkLocation);
    }

    @DeleteMapping("/departments/{departmentId}/work-locations/{workLocationId}")
    public ResponseEntity deleteWorkLocation(@PathVariable (value = "departmentId") final UUID departmentId,
                                                @PathVariable (value = "workLocationId") final UUID workLocationId) {
        workLocationService.delete(departmentId, workLocationId);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}

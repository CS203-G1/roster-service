package csd.roster.controller;

import csd.roster.exception.ResourceNotFoundException;
import csd.roster.model.WorkLocation;
import csd.roster.service.interfaces.WorkLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ROLE_EMPLOYER')")
public class WorkLocationController {
    final private WorkLocationService workLocationService;

    @Autowired
    public WorkLocationController(WorkLocationService workLocationService) {
        this.workLocationService = workLocationService;
    }

    @PostMapping("/companies/{companyId}/departments/{departmentId}/work-locations")
    public WorkLocation addWorkLocation(@PathVariable (value = "companyId") UUID companyId,
                                           @PathVariable (value = "departmentId") UUID departmentId,
                                           @RequestBody WorkLocation workLocation) {
        return workLocationService.add(companyId, departmentId, workLocation);
    }

    @GetMapping("/companies/{companyId}/departments/{departmentId}/work-locations/{workLocationId}")
    public ResponseEntity<?> getWorkLocation(@PathVariable (value = "companyId") UUID companyId,
                                             @PathVariable (value = "departmentId") UUID departmentId,
                                           @PathVariable (value = "workLocationId") UUID workLocationId) {
        try {
            WorkLocation workLocation = workLocationService.get(companyId, departmentId, workLocationId);
            return new ResponseEntity<>(workLocation, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/work-locations/{workLocationId}")
    public WorkLocation getWorkLocation(@PathVariable (value = "workLocationId") UUID workLocationId) {
        return workLocationService.getWorkLocationById(workLocationId);
    }

    @PutMapping("/companies/{companyId}/departments/{departmentId}/work-locations/{workLocationId}")
    public ResponseEntity<?> updateWorkLocation(@PathVariable (value = "companyId") UUID companyId,
                                             @PathVariable (value = "departmentId") UUID departmentId,
                                             @PathVariable (value = "workLocationId") UUID workLocationId,
                                                @RequestBody WorkLocation newWorkLocation) {
        try {
            WorkLocation workLocation = workLocationService.update(companyId, departmentId, workLocationId, newWorkLocation);
            return new ResponseEntity<>(workLocation, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/companies/{companyId}/departments/{departmentId}/work-locations/{workLocationId}")
    public ResponseEntity<?> updateWorkLocation(@PathVariable (value = "companyId") UUID companyId,
                                                @PathVariable (value = "departmentId") UUID departmentId,
                                                @PathVariable (value = "workLocationId") UUID workLocationId) {
        try {
            workLocationService.delete(companyId, departmentId, workLocationId);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}

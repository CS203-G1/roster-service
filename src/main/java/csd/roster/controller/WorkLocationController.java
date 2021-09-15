package csd.roster.controller;

import csd.roster.exception.ResourceNotFoundException;
import csd.roster.model.Department;
import csd.roster.model.WorkLocation;
import csd.roster.service.WorkLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class WorkLocationController {
    final private WorkLocationService workLocationService;

    @Autowired
    public WorkLocationController(WorkLocationService workLocationService) {
        this.workLocationService = workLocationService;
    }

    @PostMapping("/companies/{companyId}/departments/{departmentId}/work-locations")
    public ResponseEntity<?> addDepartment(@PathVariable (value = "companyId") UUID companyId,
                                           @PathVariable (value = "departmentId") UUID departmentId,
                                           @RequestBody WorkLocation workLocation) {
        try {
            WorkLocation newWorkLocation = workLocationService.add(companyId, departmentId, workLocation);
            return new ResponseEntity<>(newWorkLocation, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}

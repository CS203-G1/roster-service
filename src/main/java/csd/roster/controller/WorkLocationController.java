package csd.roster.controller;

import csd.roster.exception.ResourceNotFoundException;
import csd.roster.model.Department;
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

    public WorkLocationController(WorkLocationService workLocationService) {
        this.workLocationService = workLocationService;
    }

}

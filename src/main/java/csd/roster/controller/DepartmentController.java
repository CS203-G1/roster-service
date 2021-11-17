package csd.roster.controller;

import csd.roster.domain.exception.exceptions.ResourceNotFoundException;
import csd.roster.domain.model.Department;
import csd.roster.services.service.interfaces.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ROLE_EMPLOYER')")
public class DepartmentController {
    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping("/companies/{companyId}/departments")
    public ResponseEntity<?> addDepartment(@PathVariable (value = "companyId") final UUID companyId,
                                    @RequestBody final Department department) {
        try {
            Department newDepartment = departmentService.addDepartment(companyId, department);
            return new ResponseEntity<>(newDepartment, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/companies/{companyId}/departments/{departmentId}")
    public ResponseEntity<?> getDepartmentById(@PathVariable (value = "companyId") final UUID companyId,
                                        @PathVariable (value = "departmentId") final UUID departmentId) {
        try {
            Department newDepartment = departmentService.getDepartmentByIdAndCompanyId(departmentId, companyId);
            return new ResponseEntity<>(newDepartment, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/departments")
    public List<Department> getDepartments() {
        return departmentService.getAllDepartments();
    }

    @PutMapping("/companies/{companyId}/departments/{departmentId}")
    public ResponseEntity<?> updateDepartment(@PathVariable (value = "companyId") final UUID companyId,
                                       @PathVariable (value = "departmentId") final UUID departmentId,
                                       @RequestBody final Department department) {
        try {
            Department updatedDepartment = departmentService.updateDepartmentByIdAndDepartmentId(companyId, departmentId, department);
            return new ResponseEntity<>(updatedDepartment, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/companies/{companyId}/departments/{departmentId}")
    public ResponseEntity<?> deleteDepartment(@PathVariable (value = "companyId") final UUID companyId,
                                       @PathVariable (value = "departmentId") final UUID departmentId) {

        try {
            departmentService.deleteDepartmentByIdAndDepartmentId(companyId, departmentId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}

package csd.roster.controller;

import csd.roster.exception.CompanyNotFoundException;
import csd.roster.exception.DepartmentNotFoundException;
import csd.roster.model.Department;
import csd.roster.service.CompanyService;
import csd.roster.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class DepartmentController {
    private DepartmentService departmentService;
    private CompanyService companyService;

    @Autowired
    public DepartmentController(DepartmentService departmentService, CompanyService companyService) {
        this.departmentService = departmentService;
        this.companyService = companyService;
    }

    @PostMapping("/companies/{companyId}/departments")
    public ResponseEntity<?> addDepartment(@PathVariable (value = "companyId") UUID companyId,
                                    @RequestBody Department department) {
        try {
            Department newDepartment = departmentService.add(companyId, department);
            return new ResponseEntity<>(newDepartment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/companies/{companyId}/departments/{departmentId}")
    public ResponseEntity<?> getDepartmentById(@PathVariable (value = "companyId") UUID companyId,
                                        @PathVariable (value = "departmentId") UUID departmentId) {
        try {
            Department newDepartment = departmentService.getDepartmentByIdAndCompanyId(companyId, departmentId);
            return new ResponseEntity<>(newDepartment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/departments")
    public List<Department> getDepartments() {
        return departmentService.getAllDepartments();
    }

    @PutMapping("/companies/{companyId}/departments/{departmentId}")
    public ResponseEntity<?> updateDepartment(@PathVariable (value = "companyId") UUID companyId,
                                       @PathVariable (value = "departmentId") UUID departmentId,
                                       @RequestBody Department department) {
        try {
            Department updatedDepartment = departmentService.update(companyId, departmentId, department);
            return new ResponseEntity<>(updatedDepartment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/companies/{companyId}/departments/{departmentId}")
    public ResponseEntity<?> deleteDepartment(@PathVariable (value = "companyId") UUID companyId,
                                       @PathVariable (value = "departmentId") UUID departmentId) {

        try {
            departmentService.delete(companyId, departmentId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}

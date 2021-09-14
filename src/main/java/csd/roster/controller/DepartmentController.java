package csd.roster.controller;

import csd.roster.exception.CompanyNotFoundException;
import csd.roster.exception.DepartmentNotFoundException;
import csd.roster.model.Department;
import csd.roster.service.CompanyService;
import csd.roster.service.DepartmentService;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Department addDepartment(@PathVariable (value = "companyId") UUID companyId,
                                    @RequestBody Department department) {
        return companyService.getCompanyById(companyId).map(company -> {
            department.setCompany(company);
            return departmentService.addDepartment(department);
        }).orElseThrow(() -> new CompanyNotFoundException(companyId));

    }

    @GetMapping("/companies/{companyId}/departments/{id}")
    public Department getDepartmentById(@PathVariable UUID companyId, @PathVariable UUID departmentId) {
        if (companyService.getCompanyById(companyId) == null)
            throw new CompanyNotFoundException(companyId);

        return departmentService.getDepartmentByIdAndCompanyId(departmentId, companyId)
                .orElseThrow(() -> new DepartmentNotFoundException(companyId, departmentId));
    }

    @GetMapping("/departments")
    public List<Department> getDepartments() {
        return departmentService.getAllDepartments();
    }

    @PutMapping("/companies/{companyId}/departments/{id}")
    public Department updateDepartment(@PathVariable UUID companyId,
                                       @PathVariable UUID departmentId,
                                       @RequestBody Department department) {
        throw new NotYetImplementedException();
    }

    @DeleteMapping("/companies/{companyId}/departments/{id}")
    public Department deleteDepartment(@PathVariable UUID companyId,
                                       @PathVariable UUID departmentId,
                                       @RequestBody Department department) {
        throw new NotYetImplementedException();
    }
}

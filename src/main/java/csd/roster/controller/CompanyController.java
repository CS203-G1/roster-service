package csd.roster.controller;

import csd.roster.exception.CompanyNotFoundException;
import csd.roster.exception.ResourceNotFoundException;
import csd.roster.model.Company;
import csd.roster.service.interfaces.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ROLE_EMPLOYER')")
public class CompanyController {
    private CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public Company addCompany(@RequestBody Company company) {
        return companyService.addCompany(company);
    }

    @GetMapping("/companies/{id}")
    public Company getCompanyById(@PathVariable UUID id) {
        return companyService.getCompanyById(id);
    }

    @GetMapping("/companies")
    public List<Company> getCompanies() {
        return companyService.getAllCompanies();
    }

    @DeleteMapping("/companies/{companyId}")
    public void delete(@PathVariable UUID companyId) {
        companyService.deleteCompanyByid(companyId);
    }
}

package csd.roster.controller;

import csd.roster.domain.model.Company;
import csd.roster.services.service.interfaces.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ROLE_EMPLOYER')")
public class CompanyController {
    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public Company addCompany(@RequestBody final Company company) {
        return companyService.addCompany(company);
    }

    @GetMapping("/companies/{id}")
    public Company getCompanyById(@PathVariable final UUID id) {
        return companyService.getCompanyById(id);
    }

    @GetMapping("/companies")
    public List<Company> getCompanies() {
        return companyService.getAllCompanies();
    }

    @DeleteMapping("/companies/{companyId}")
    public void delete(@PathVariable final UUID companyId) {
        companyService.deleteCompanyByid(companyId);
    }
}

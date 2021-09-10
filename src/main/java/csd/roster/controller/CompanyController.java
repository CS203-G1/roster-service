package csd.roster.controller;

import csd.roster.model.Company;
import csd.roster.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
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
}

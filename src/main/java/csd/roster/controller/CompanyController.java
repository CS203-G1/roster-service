package csd.roster.controller;

import csd.roster.exception.CompanyNotFoundException;
import csd.roster.exception.ResourceNotFoundException;
import csd.roster.model.Company;
import csd.roster.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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
    public ResponseEntity<?> getCompanyById(@PathVariable UUID id) {
        try {
            return new ResponseEntity<>(companyService.getCompanyById(id), HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/companies")
    public List<Company> getCompanies() {
        return companyService.getAllCompanies();
    }
}

package csd.roster.service;

import csd.roster.model.Company;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyService {
    Company addCompany(Company company);

    List<Company> getAllCompanies();

    Optional<Company> getCompanyById(UUID id);

    Company updateCompanyByid(UUID id, Company company);

    Company deleteCompanyByid(UUID id);
}
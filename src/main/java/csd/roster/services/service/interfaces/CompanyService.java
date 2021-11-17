package csd.roster.services.service.interfaces;

import csd.roster.domain.model.Company;

import java.util.List;
import java.util.UUID;

public interface CompanyService {
    Company addCompany(Company company);

    List<Company> getAllCompanies();

    Company getCompanyById(UUID id);

    Company updateCompanyById(UUID id, Company company);

    void deleteCompanyById(UUID id);
}

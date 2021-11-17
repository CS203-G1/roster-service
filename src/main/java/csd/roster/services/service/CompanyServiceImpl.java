package csd.roster.services.service;

import csd.roster.domain.exception.exceptions.CompanyNotFoundException;
import csd.roster.domain.model.Company;
import csd.roster.repo.repository.CompanyRepository;
import csd.roster.services.service.interfaces.CompanyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Company addCompany(final Company company) {
        return companyRepository.save(company);
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public Company getCompanyById(final UUID id) throws CompanyNotFoundException {
        return companyRepository.findById(id).orElseThrow(() -> new CompanyNotFoundException(id));
    }

    @Override
    public Company updateCompanyById(final UUID id, final Company company) {
        return null;
    }

    @Override
    public void deleteCompanyById(final UUID id) throws CompanyNotFoundException {
        Company company = getCompanyById(id);

        companyRepository.delete(company);
    }
}

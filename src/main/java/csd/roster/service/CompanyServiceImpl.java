package csd.roster.service;

import csd.roster.exception.exceptions.CompanyNotFoundException;
import csd.roster.model.Company;
import csd.roster.repository.CompanyRepository;
import csd.roster.service.interfaces.CompanyService;
import org.hibernate.cfg.NotYetImplementedException;
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
    public Company updateCompanyByid(final UUID id, final Company company) {
        return null;
    }

    @Override
    public void deleteCompanyByid(final UUID id) throws CompanyNotFoundException {
        Company company = getCompanyById(id);

        companyRepository.delete(company);
    }
}

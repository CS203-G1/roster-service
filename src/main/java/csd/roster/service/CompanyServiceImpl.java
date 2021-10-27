package csd.roster.service;

import csd.roster.exception.CompanyNotFoundException;
import csd.roster.model.Company;
import csd.roster.repository.CompanyRepository;
import csd.roster.service.interfaces.CompanyService;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CompanyServiceImpl implements CompanyService {

    private CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Company addCompany(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public Company getCompanyById(UUID id) throws CompanyNotFoundException {
        return companyRepository.findById(id).orElseThrow(() -> new CompanyNotFoundException(id));
    }

    @Override
    public Company updateCompanyByid(UUID id, Company company) {
        throw new NotYetImplementedException();
    }

    @Override
    public void deleteCompanyByid(UUID id) throws CompanyNotFoundException {
        Company company = getCompanyById(id);

        companyRepository.delete(company);
    }
}

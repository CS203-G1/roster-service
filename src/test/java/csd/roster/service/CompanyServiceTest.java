package csd.roster.service;

import csd.roster.model.Company;
import csd.roster.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companies;

    @InjectMocks
    private CompanyServiceImpl companyService;

    @Test
    void addCompany_NewCompany_ReturnSavedCompany(){
        Company company = new Company(UUID.fromString("a4ccc2c4-0426-41a2-b904-f7a941ba27e0"), "Eppal", null);

        when(companies.save(any(Company.class))).thenReturn(company);

        Company savedCompany = companyService.addCompany(company);

        assertNotNull(savedCompany);

        verify(companies, times(1)).save(company);

    }

    @Test
    void getCompanyById_CompanyExists_ReturnCompany(){
        UUID id = UUID.fromString("a4ccc2c4-0426-41a2-b904-f7a941ba27e0");
        Company company = new Company(id, "Eppal", null);

        when(companies.findById(any(UUID.class))).thenReturn(java.util.Optional.of(company));

        Company foundCompany = companyService.getCompanyById(id);

        assertNotNull(foundCompany);
        assertEquals(company, foundCompany);

        verify(companies, times(1)).findById(id);

    }

    @Test
    void getAllCompanies_AfterAddingThreeCompanies_ReturnListOfCompanies(){
        UUID id1 = UUID.fromString("a4ccc2c4-0426-41a2-b904-f7a941ba27e0");
        Company company1 = new Company(id1, "Eppal", null);

        UUID id2 = UUID.fromString("a4ccc2c4-0426-41a2-b904-f7a941ba27e0");
        Company company2 = new Company(id1, "PayPal", null);

        UUID id3 = UUID.fromString("a4ccc2c4-0426-41a2-b904-f7a941ba27e0");
        Company company3 = new Company(id1, "PeePal", null);

        when(companies.findAll()).thenReturn(new ArrayList<Company>());
        when(companies.save(any(Company.class)))
                .thenReturn(company1)
                .thenReturn(company2)
                .thenReturn(company3);

        List<Company> allCompanies = companyService.getAllCompanies();
        Company savedCompany1 = companyService.addCompany(company1);
        Company savedCompany2 = companyService.addCompany(company2);
        Company savedCompany3 = companyService.addCompany(company3);

        allCompanies.add(savedCompany1);
        allCompanies.add(savedCompany2);
        allCompanies.add(savedCompany3);

        assertEquals(company1,savedCompany1);
        assertEquals(company2,savedCompany2);
        assertEquals(company3,savedCompany3);

        assertNotNull(allCompanies);
        assertEquals(3,allCompanies.size());

        verify(companies, times(3)).save(any(Company.class));
        verify(companies, times(1)).findAll();

    }
//    @Test
//    void updateCompanyById_CompanyExists_ReturnUpdatedCompany(){
//        UUID id = UUID.fromString("a4ccc2c4-0426-41a2-b904-f7a941ba27e0");
//        Company company = new Company(id, "Eppal", null);
//
//
//    }
}

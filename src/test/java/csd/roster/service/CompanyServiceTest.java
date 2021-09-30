package csd.roster.service;

import csd.roster.model.Company;
import csd.roster.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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
    void addCompany_ReturnSavedCompany(){
        Company company = new Company(UUID.fromString("a4ccc2c4-0426-41a2-b904-f7a941ba27e0"), "Eppal", null);

        when(companies.save(any(Company.class))).thenReturn(company);

        Company savedCompany = companyService.addCompany(company);

        assertNotNull(savedCompany);

        verify(companies, times(1)).save(company);

    }

    @Test
    void getCompanyByID(){
        UUID id = UUID.fromString("a4ccc2c4-0426-41a2-b904-f7a941ba27e0");
        Company company = new Company(UUID.fromString("a4ccc2c4-0426-41a2-b904-f7a941ba27e0"), "Eppal", null);

        when(companies.findById(any(UUID.class))).thenReturn(java.util.Optional.of(company));

        Company foundCompany = companyService.getCompanyById(id);

        assertNotNull(foundCompany);
        assertEquals(company, foundCompany);

        verify(companies, times(1)).findById(id);

    }
}

package csd.roster.service;

import csd.roster.exception.CompanyNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
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
        UUID id = UUID.randomUUID();
        Company company = new Company(id, "Eppal", null);

        when(companies.save(any(Company.class))).thenReturn(company);

        Company savedCompany = companyService.addCompany(company);

        assertNotNull(savedCompany);

        verify(companies, times(1)).save(company);

    }

    @Test
    void getCompanyById_CompanyExists_ReturnCompany(){
        UUID id = UUID.randomUUID();
        Company company = new Company(id, "Eppal", null);

        when(companies.findById(any(UUID.class))).thenReturn(java.util.Optional.of(company));

        Company foundCompany = companyService.getCompanyById(id);

        assertNotNull(foundCompany);
        assertEquals(company, foundCompany);

        verify(companies, times(1)).findById(id);

    }

    @Test
    void getAllCompanies_NoCompanies_ReturnEmptyList(){
        when(companies.findAll()).thenReturn(new ArrayList<Company>());


        List<Company> allCompanies = companyService.getAllCompanies();

        assertNotNull(allCompanies);
        assertEquals(0,allCompanies.size());
        verify(companies, times(1)).findAll();
    }

    @Test
    void getCompanyByID_CompanyDoesNotExist_ThrowException(){
        UUID id = UUID.randomUUID();
        Exception exception = assertThrows(CompanyNotFoundException.class, () -> companyService.getCompanyById(id));

        assertEquals("Could not find company " + id, exception.getMessage());
        verify(companies,times(1)).findById(any(UUID.class));
    }
//    @Test
//    void updateCompanyById_CompanyExists_ReturnUpdatedCompany(){
//        UUID id = UUID.fromString("a4ccc2c4-0426-41a2-b904-f7a941ba27e0");
//        Company company = new Company(id, "Eppal", null);
//
//
//    }
}
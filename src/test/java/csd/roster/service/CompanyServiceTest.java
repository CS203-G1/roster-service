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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companies;

    @InjectMocks
    private CompanyServiceImpl companyService;

    @Test
    void addCompany_newName_ReturnSavedCompany(){
        // Arrange
        Company company = new Company(UUID.fromString("a4ccc2c4-0426-41a2-b904-f7a941ba27e0"), "Eppal", null);

        when(companies.save(any(Company.class))).thenReturn(company);

        // Act
        Company savedCompany = companyService.addCompany(company);

        // Assert
        assertNotNull(savedCompany);




    }
}

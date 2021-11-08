package csd.roster.integrations;

import csd.roster.RosterServiceApplication;
import csd.roster.configurations.IntegrationTestConfig;
import csd.roster.model.Company;
import csd.roster.repository.CompanyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {RosterServiceApplication.class, IntegrationTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class CompanyIntegrationTest {

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CompanyRepository companyRepository;

    @BeforeEach
    public void setUp(){
        Company company = new Company();
        company.setName("Test Company 1");
        companyRepository.save(company);
    }

    @AfterEach
    public void tearDown(){
        companyRepository.deleteAll();
    }

    @Test
    public void getCompany_CompanyExists_ReturnFoundCompany(){
        try {
            // Return first Company in the database
            Company firstCompany = companyRepository.findAll().get(0);
            URI uri = new URI(baseUrl + port + "/companies/" + firstCompany.getId());

            // Passing Cognito jwt token into headers
            String accessToken = "";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer "+accessToken);

            HttpEntity<String> entity = new HttpEntity<String>("parameters",headers);
            ResponseEntity<Company> result = restTemplate.exchange(uri, HttpMethod.GET, entity, Company.class);

            assertEquals(200, result.getStatusCode().value());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }


}

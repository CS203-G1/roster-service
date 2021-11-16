package csd.roster.integrations;

import csd.roster.RosterServiceApplication;
import csd.roster.configurations.IntegrationTestConfig;
import csd.roster.domain.model.Company;
import csd.roster.repo.repository.CompanyRepository;
import csd.roster.repo.util.AwsCognitoUtil;
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
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {RosterServiceApplication.class, IntegrationTestConfig.class},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class CompanyIntegrationTest {

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private AwsCognitoUtil awsCognitoUtil;

    private String accessToken;

    @BeforeEach
    public void setUp(){
        this.accessToken = awsCognitoUtil.authenticateAndGetToken();

        Company company = new Company();
        company.setName("Test Company 1");
        companyRepository.save(company);
    }

    @AfterEach
    public void tearDown(){
        companyRepository.deleteAll();
    }

    @Test
    public void getCompanyById_CompanyExists_ReturnFoundCompany(){
        try {
            // Return first Company in the database
            Company firstCompany = companyRepository.findAll().get(0);
            URI uri = new URI(baseUrl + port + "/companies/" + firstCompany.getId());

            // Passing Cognito jwt token into headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer "+ this.accessToken);

            HttpEntity<String> entity = new HttpEntity<String>("parameters",headers);
            ResponseEntity<Company> result = restTemplate.exchange(uri, HttpMethod.GET, entity, Company.class);

            assertEquals(200, result.getStatusCode().value());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getCompanyById_CompanyDoesNotExist_Return404() {
        try {
            URI uri = new URI(baseUrl + port + "/companies/" + UUID.randomUUID());

            // Passing Cognito jwt token into headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + this.accessToken);

            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            assertEquals(404, result.getStatusCode().value());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCompanyById_NoPermissions_Return401(){
        try {
            URI uri = new URI(baseUrl + port + "/companies/" + UUID.randomUUID());
            ResponseEntity<Company> result = restTemplate.getForEntity(uri, Company.class);

            assertEquals(401, result.getStatusCode().value());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addCompany_NewCompany_Return200(){
        try {
            URI uri = new URI(baseUrl + port + "/companies/");
            Company company = new Company();
            company.setName("New Company 1");

            // Passing Cognito jwt token into headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + this.accessToken);

            HttpEntity<Company> entity = new HttpEntity<Company>(company, headers);
            ResponseEntity<Company> result = restTemplate.exchange(uri, HttpMethod.POST, entity, Company.class);

            assertEquals(200, result.getStatusCode().value());
            assertEquals("New Company 1", result.getBody().getName());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void deleteCompany_companyExists_Return200(){
        try {
            Company firstCompany = companyRepository.findAll().get(0);
            URI uri = new URI(baseUrl + port + "/companies/" + firstCompany.getId());

            // Passing Cognito jwt token into headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + this.accessToken);

            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            ResponseEntity<Company> result = restTemplate.exchange(uri, HttpMethod.DELETE, entity, Company.class);

            assertEquals(200, result.getStatusCode().value());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteCompany_companyDoesNotExist_Return404(){
        try {
            URI uri = new URI(baseUrl + port + "/companies/" + UUID.randomUUID());

            // Passing Cognito jwt token into headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + this.accessToken);

            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);

            assertEquals(404, result.getStatusCode().value());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addCompany_getCompany_deleteCompany_newCompany_Return200() throws URISyntaxException {
        // Add new Company
        Company savedCompany = null;
        {
            URI uri = new URI(baseUrl + port + "/companies/");
            Company company = new Company();
            company.setName("New Company 1");

            // Passing Cognito jwt token into headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + this.accessToken);

            HttpEntity<Company> entity = new HttpEntity<Company>(company, headers);
            ResponseEntity<Company> result = restTemplate.exchange(uri, HttpMethod.POST, entity, Company.class);

            assertEquals(200, result.getStatusCode().value());
            assertEquals("New Company 1", result.getBody().getName());

            savedCompany = result.getBody();
        }

        // Get Company
        {
            URI uri = new URI(baseUrl + port + "/companies/" + savedCompany.getId());

            // Passing Cognito jwt token into headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + this.accessToken);

            HttpEntity<String> entity = new HttpEntity<String>("parameters",headers);
            ResponseEntity<Company> result = restTemplate.exchange(uri, HttpMethod.GET, entity, Company.class);

            assertEquals(200, result.getStatusCode().value());
            assertEquals("New Company 1", result.getBody().getName());
        }

        //Delete Company
        {
            URI uri = new URI(baseUrl + port + "/companies/" + savedCompany.getId());

            // Passing Cognito jwt token into headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + this.accessToken);

            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            ResponseEntity<Company> result = restTemplate.exchange(uri, HttpMethod.DELETE, entity, Company.class);

            assertEquals(200, result.getStatusCode().value());
        }
    }

}
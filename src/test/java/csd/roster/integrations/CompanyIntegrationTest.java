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
            String accessToken = "eyJraWQiOiJESCtSN05oekRDXC9tKzNDbVhKRXM0M08zRDdNR0o3dnQ4ZDAxelVsQmZUND0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI4Y2RiZjNmNS00Y2FlLTRjMjAtYWRjZC05ZWU3MzZjMTgxM2MiLCJjb2duaXRvOmdyb3VwcyI6WyJST0xFX0VNUExPWUVFIiwiUk9MRV9TVVBFUl9VU0VSIiwiUk9MRV9FTVBMT1lFUiJdLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAuYXAtc291dGhlYXN0LTEuYW1hem9uYXdzLmNvbVwvYXAtc291dGhlYXN0LTFfcFpvSXc5a2lOIiwiY2xpZW50X2lkIjoiNHM4dG90ZmlvdmxrNzgydWo5OGNqZGpncDAiLCJvcmlnaW5fanRpIjoiZGJkMTExMTMtNThiZS00MWFhLTgyMzktMzZjZjU2N2UxY2IwIiwiZXZlbnRfaWQiOiI3ZTMwNDYzMS04M2EyLTRlZWEtYjJmYi1hZmIwZDIxY2VmZTgiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6ImF3cy5jb2duaXRvLnNpZ25pbi51c2VyLmFkbWluIiwiYXV0aF90aW1lIjoxNjM2Mzc5NDY2LCJleHAiOjE2MzYzODMwNjYsImlhdCI6MTYzNjM3OTQ2NiwianRpIjoiMGZkZGI4YTYtZGZmMy00ZDZiLWFiNWEtZjRjNzMxMTdjODkwIiwidXNlcm5hbWUiOiI4Y2RiZjNmNS00Y2FlLTRjMjAtYWRjZC05ZWU3MzZjMTgxM2MifQ.CnVtt3islh9EtJUwQD1aOQDEhK0N5liiKGGLGOiQPhgIfvCKuYgMmdI3mBa43lNo1KbQkvsNkhKUkrf58Rw5YV9wLGIneUpoDlA9orCaOB4g5MKKYe5Ca12I54h3yM7vNCLHXoMFKn0QTMXmXekPaL3NOvLhCMneW-WRHCV6Jbcz35kJm93QHYFYB7NwoibDP12yhj8uYulYGfNp3JBRX1VjkVb2jSEt_rx836MnMDd3g5mE2jCwY9k-fRDcCCmufpNttKMouXfSB8qiReb836kxPPglsotFUqZO-91ywK19wghZeEDOfIDZ4ssMqsKZf9rqrBDMCMf3IXUrWZTsQg";
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

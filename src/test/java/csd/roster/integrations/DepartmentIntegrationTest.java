package csd.roster.integrations;

import csd.roster.RosterServiceApplication;
import csd.roster.configurations.IntegrationTestConfig;
import csd.roster.exception.exceptions.CompanyNotFoundException;
import csd.roster.exception.exceptions.DepartmentNotFoundException;
import csd.roster.model.Company;
import csd.roster.model.Department;
import csd.roster.repository.CompanyRepository;
import csd.roster.repository.DepartmentRepository;
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

@SpringBootTest(classes = { RosterServiceApplication.class, IntegrationTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class DepartmentIntegrationTest {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private String accessToken = "";

    @BeforeEach
    public void setUp(){
        Company company = new Company();
        company.setName("Test Company 1");
        Company savedCompany = companyRepository.save(company);
        Department department = new Department();
        department.setCompany(savedCompany);
        department.setName("Test Department 1");
        departmentRepository.save(department);
    }

    @AfterEach
    public void tearDown(){
        departmentRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void getDepartment_CompanyDoesNotExist_Return404() throws URISyntaxException {
        UUID companyId = UUID.randomUUID();
        URI uri = new URI(baseUrl + port + "/companies/" + companyId + "/departments/" + UUID.randomUUID());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        CompanyNotFoundException e = new CompanyNotFoundException(companyId);

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getDepartment_DepartmentDoesNotExist_Return404() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        UUID departmentId = UUID.randomUUID();
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/" + departmentId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        DepartmentNotFoundException e = new DepartmentNotFoundException(departmentId,company.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getDepartment_DepartmentExists_Return200() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        Department firstDepartment = departmentRepository.findAll().get(0);
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/" + firstDepartment.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<Department> result = restTemplate.exchange(uri, HttpMethod.GET, entity, Department.class);


        assertEquals(firstDepartment, result.getBody());
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void addDepartment_CompanyDoesNotExist_Return404() throws URISyntaxException {
        UUID companyId = UUID.randomUUID();
        Department department = new Department();

        URI uri = new URI(baseUrl + port + "/companies/" + companyId + "/departments/");


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<Department> entity = new HttpEntity<>(department, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        CompanyNotFoundException e = new CompanyNotFoundException(companyId);

        assertEquals(result.getBody(), e.getMessage());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void addDepartment_DepartmentAdded_Return200() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        Department department = new Department();
        department.setName("Added Department");

        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/");


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<Department> entity = new HttpEntity<>(department, headers);
        ResponseEntity<Department> result = restTemplate.exchange(uri, HttpMethod.POST, entity, Department.class);


        assertEquals(department.getName(), result.getBody().getName());
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void updateCompany_CompanyDoesNotExist_Return404() throws URISyntaxException {
        UUID companyId = UUID.randomUUID();
        Department department = new Department();
        department.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/companies/" + companyId + "/departments/" + department.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<Department> entity = new HttpEntity<Department>(department, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);

        CompanyNotFoundException e = new CompanyNotFoundException(companyId);

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void updateCompany_DepartmentDoesNotExist_Return404() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        Department department = new Department();
        department.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/" + department.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<Department> entity = new HttpEntity<Department>(department, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);

        DepartmentNotFoundException e = new DepartmentNotFoundException(department.getId(),company.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void updateCompany_DepartmentUpdated_Return200() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        Department department = departmentRepository.findAll().get(0);
        department.setName("Updated Department");
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/" + department.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<Department> entity = new HttpEntity<Department>(department, headers);
        ResponseEntity<Department> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, Department.class);



        assertEquals("Updated Department", result.getBody().getName());
        assertEquals(200, result.getStatusCode().value());
    }

}
package csd.roster.integrations;

import csd.roster.RosterServiceApplication;
import csd.roster.configurations.IntegrationTestConfig;
import csd.roster.exception.exceptions.CompanyNotFoundException;
import csd.roster.exception.exceptions.DepartmentNotFoundException;
import csd.roster.exception.exceptions.WorkLocationNotFoundException;
import csd.roster.model.Company;
import csd.roster.model.Department;
import csd.roster.model.WorkLocation;
import csd.roster.repository.CompanyRepository;
import csd.roster.repository.DepartmentRepository;
import csd.roster.repository.WorkLocationRepository;
import org.hibernate.jdbc.Work;
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
public class WorkLocationIntegrationTest {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private WorkLocationRepository workLocationRepository;

    private String accessToken = "";
    
    @BeforeEach
    public void setUp(){
        Company company = new Company();
        company.setName("Test Company 1");
        Company savedCompany = companyRepository.save(company);

        Department department = new Department();
        department.setCompany(savedCompany);
        department.setName("Test Department 1");
        Department savedDepartment = departmentRepository.save(department);

        WorkLocation workLocation = new WorkLocation();
        workLocation.setDepartment(savedDepartment);
        workLocation.setName("Test Work Location 1");
        WorkLocation savedWorkLocation = workLocationRepository.save(workLocation);
    }

    @AfterEach
    public void tearDown(){
        workLocationRepository.deleteAll();
        departmentRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void getWorkLocation_CompanyDoesNotExist_Return404() throws URISyntaxException {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        Department department = new Department();
        department.setId(UUID.randomUUID());
        WorkLocation workLocation = new WorkLocation();
        workLocation.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/" + department.getId() + "/work-locations/" + workLocation.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        CompanyNotFoundException e = new CompanyNotFoundException(company.getId());

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getWorkLocation_DepartmentDoesNotExist_Return404() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        Department department = new Department();
        department.setId(UUID.randomUUID());
        WorkLocation workLocation = new WorkLocation();
        workLocation.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/" + department.getId() + "/work-locations/" + workLocation.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        DepartmentNotFoundException e = new DepartmentNotFoundException(department.getId(),company.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getWorkLocation_WorkLocationDoesNotExist_Return404() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        Department department = departmentRepository.findAll().get(0);
        WorkLocation workLocation = new WorkLocation();
        workLocation.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/" + department.getId() + "/work-locations/" + workLocation.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        WorkLocationNotFoundException e = new WorkLocationNotFoundException(department.getId(), company.getId(), workLocation.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getWorkLocation_WorkLocationExists_Return200() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        Department department = departmentRepository.findAll().get(0);
        WorkLocation workLocation = workLocationRepository.findAll().get(0);
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/" + department.getId() + "/work-locations/" + workLocation.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<WorkLocation> result = restTemplate.exchange(uri, HttpMethod.GET, entity, WorkLocation.class);


        assertEquals(workLocation, result.getBody());
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void addWorkLocation_CompanyDoesNotExist_Return404() throws URISyntaxException {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        Department department = new Department();
        department.setId(UUID.randomUUID());
        WorkLocation workLocation = new WorkLocation();
        workLocation.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/" + department.getId() + "/work-locations/" );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<WorkLocation> entity = new HttpEntity<WorkLocation>(workLocation, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        CompanyNotFoundException e = new CompanyNotFoundException(company.getId());

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void addWorkLocation_DepartmentDoesNotExist_Return404() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        Department department = new Department();
        department.setId(UUID.randomUUID());
        WorkLocation workLocation = new WorkLocation();
        workLocation.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/" + department.getId() + "/work-locations/");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<WorkLocation> entity = new HttpEntity<WorkLocation>(workLocation, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        DepartmentNotFoundException e = new DepartmentNotFoundException(department.getId(),company.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void addWorkLocation_WorkLocationAdded_Return200() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        Department department = departmentRepository.findAll().get(0);
        WorkLocation workLocation = new WorkLocation();
        workLocation.setName("Added Department 1");
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/" + department.getId() + "/work-locations/");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<WorkLocation> entity = new HttpEntity<WorkLocation>(workLocation, headers);
        ResponseEntity<WorkLocation> result = restTemplate.exchange(uri, HttpMethod.POST, entity, WorkLocation.class);


        assertEquals("Added Department 1", result.getBody().getName());
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void updateWorkLocation_CompanyDoesNotExist_Return404() throws URISyntaxException {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        Department department = new Department();
        department.setId(UUID.randomUUID());
        WorkLocation workLocation = new WorkLocation();
        workLocation.setId(UUID.randomUUID());
        workLocation.setName("Updated Work Location");
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/" + department.getId() + "/work-locations/" + workLocation.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<WorkLocation> entity = new HttpEntity<WorkLocation>(workLocation, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);

        CompanyNotFoundException e = new CompanyNotFoundException(company.getId());

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void updateWorkLocation_DepartmentDoesNotExist_Return404() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        Department department = new Department();
        department.setId(UUID.randomUUID());
        WorkLocation workLocation = new WorkLocation();
        workLocation.setId(UUID.randomUUID());
        workLocation.setName("Updated Work Location");
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/" + department.getId() + "/work-locations/" + workLocation.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<WorkLocation> entity = new HttpEntity<WorkLocation>(workLocation, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);

        DepartmentNotFoundException e = new DepartmentNotFoundException(department.getId(),company.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void updateWorkLocation_WorkLocationDoesNotExist_Return404() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        Department department = departmentRepository.findAll().get(0);
        WorkLocation workLocation = new WorkLocation();
        workLocation.setId(UUID.randomUUID());
        workLocation.setName("Updated Work Location");
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/" + department.getId() + "/work-locations/" + workLocation.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<WorkLocation> entity = new HttpEntity<WorkLocation>(workLocation, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);

        WorkLocationNotFoundException e = new WorkLocationNotFoundException(department.getId(), company.getId(), workLocation.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void updateWorkLocation_WorkLocationExists_Return200() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        Department department = departmentRepository.findAll().get(0);
        WorkLocation workLocation = workLocationRepository.findAll().get(0);
        workLocation.setName("Updated Work Location");
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/departments/" + department.getId() + "/work-locations/" + workLocation.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<WorkLocation> entity = new HttpEntity<WorkLocation>(workLocation, headers);
        ResponseEntity<WorkLocation> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, WorkLocation.class);


        assertEquals("Updated Work Location", result.getBody().getName());
        assertEquals(200, result.getStatusCode().value());
    }
}

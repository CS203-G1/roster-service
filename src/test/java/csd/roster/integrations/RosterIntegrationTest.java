package csd.roster.integrations;

import csd.roster.RosterServiceApplication;
import csd.roster.configurations.IntegrationTestConfig;

import csd.roster.domain.exception.exceptions.CompanyNotFoundException;
import csd.roster.domain.exception.exceptions.RosterNotFoundException;
import csd.roster.domain.exception.exceptions.WorkLocationNotFoundException;
import csd.roster.domain.model.Company;
import csd.roster.domain.model.Department;
import csd.roster.domain.model.Roster;
import csd.roster.domain.model.WorkLocation;
import csd.roster.repo.repository.CompanyRepository;
import csd.roster.repo.repository.DepartmentRepository;
import csd.roster.repo.repository.RosterRepository;
import csd.roster.repo.repository.WorkLocationRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = { RosterServiceApplication.class, IntegrationTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class RosterIntegrationTest {
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

    @Autowired
    private RosterRepository rosterRepository;

    @Autowired
    private AwsCognitoUtil awsCognitoUtil;

    private String accessToken;

    public RosterIntegrationTest() {
    }

    @BeforeEach
    public void setUp(){
        this.accessToken = awsCognitoUtil.authenticateAndGetToken();
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

        Roster roster = new Roster();
        roster.setWorkLocation(savedWorkLocation);
        roster.setDate(LocalDate.now().plusDays(1));
        roster.setFromDateTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN));
        roster.setToDateTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MAX));
        Roster savedRoster = rosterRepository.save(roster);
    }

    @AfterEach
    public void tearDown(){
        rosterRepository.deleteAll();
        workLocationRepository.deleteAll();
        departmentRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void getRoster_WorkLocationDoesNotExist_Return404() throws URISyntaxException {
        WorkLocation workLocation = new WorkLocation();
        workLocation.setId(UUID.randomUUID());
        Roster roster = new Roster();
        roster.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/work-locations/" + workLocation.getId() + "/rosters/" + roster.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        WorkLocationNotFoundException e = new WorkLocationNotFoundException(workLocation.getId());

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getRoster_RosterDoesNotExist_Return404() throws URISyntaxException {
        WorkLocation workLocation = workLocationRepository.findAll().get(0);
        Roster roster = new Roster();
        roster.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/work-locations/" + workLocation.getId() + "/rosters/" + roster.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        RosterNotFoundException e = new RosterNotFoundException(roster.getId(), workLocation.getId());

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getRoster_RosterExists_Return200() throws URISyntaxException {
        WorkLocation workLocation = workLocationRepository.findAll().get(0);
        Roster roster = rosterRepository.findAll().get(0);
        URI uri = new URI(baseUrl + port + "/work-locations/" + workLocation.getId() + "/rosters/" + roster.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<Roster> result = restTemplate.exchange(uri, HttpMethod.GET, entity, Roster.class);


        assertEquals(roster, result.getBody());
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void addRoster_WorkLocationDoesNotExist_Return404() throws URISyntaxException {
        WorkLocation workLocation = new WorkLocation();
        workLocation.setId(UUID.randomUUID());
        Roster roster = new Roster();
        roster.setId(UUID.randomUUID());
        roster.setWorkLocation(workLocation);
        roster.setDate(LocalDate.now().plusDays(1));
        roster.setFromDateTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN));
        roster.setToDateTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MAX));
        URI uri = new URI(baseUrl + port + "/work-locations/" + workLocation.getId() + "/rosters/");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<Roster> entity = new HttpEntity<Roster>(roster, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        WorkLocationNotFoundException e = new WorkLocationNotFoundException(workLocation.getId());

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void addRoster_InvalidRoster_Return400() throws URISyntaxException {
        WorkLocation workLocation = workLocationRepository.findAll().get(0);
        Roster roster = new Roster();
        roster.setId(UUID.randomUUID());
        roster.setWorkLocation(workLocation);
        roster.setDate(LocalDate.now().plusDays(-1));
        roster.setFromDateTime(LocalDateTime.of(LocalDate.now().plusDays(-1), LocalTime.MIN));
        roster.setToDateTime(LocalDateTime.of(LocalDate.now().plusDays(-1), LocalTime.MAX));
        URI uri = new URI(baseUrl + port + "/work-locations/" + workLocation.getId() + "/rosters/");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<Roster> entity = new HttpEntity<Roster>(roster, headers);
        ResponseEntity<Roster> result = restTemplate.exchange(uri, HttpMethod.POST, entity, Roster.class);


        assertEquals(400, result.getStatusCode().value());
    }

    @Test
    public void addRoster_WorkLocationAdded_Return200() throws URISyntaxException {
        WorkLocation workLocation = workLocationRepository.findAll().get(0);
        Roster roster = new Roster();
        roster.setId(UUID.randomUUID());
        roster.setWorkLocation(workLocation);
        roster.setDate(LocalDate.now().plusDays(1));
        roster.setFromDateTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN));
        roster.setToDateTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MAX));
        URI uri = new URI(baseUrl + port + "/work-locations/" + workLocation.getId() + "/rosters/");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<Roster> entity = new HttpEntity<Roster>(roster, headers);
        ResponseEntity<Roster> result = restTemplate.exchange(uri, HttpMethod.POST, entity, Roster.class);


        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void updateRoster_WorkLocationDoesNotExist_Return404() throws URISyntaxException {
        WorkLocation workLocation = new WorkLocation();
        workLocation.setId(UUID.randomUUID());
        Roster roster = new Roster();
        roster.setId(UUID.randomUUID());
        roster.setDate(LocalDate.now().plusDays(1));
        roster.setFromDateTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN));
        roster.setToDateTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MAX));
        URI uri = new URI(baseUrl + port + "/work-locations/" + workLocation.getId() + "/rosters/" + roster.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<Roster> entity = new HttpEntity<Roster>(roster, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);

        WorkLocationNotFoundException e = new WorkLocationNotFoundException(workLocation.getId());

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void updateRoster_RosterDoesNotExist_Return404() throws URISyntaxException {
        WorkLocation workLocation = workLocationRepository.findAll().get(0);
        Roster roster = new Roster();
        roster.setId(UUID.randomUUID());
        roster.setDate(LocalDate.now().plusDays(1));
        roster.setFromDateTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN));
        roster.setToDateTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MAX));
        URI uri = new URI(baseUrl + port + "/work-locations/" + workLocation.getId() + "/rosters/" + roster.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<Roster> entity = new HttpEntity<Roster>(roster, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);

        RosterNotFoundException e = new RosterNotFoundException(roster.getId(), workLocation.getId());

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void updateRoster_RosterDoesExists_Return200() throws URISyntaxException {
        WorkLocation workLocation = workLocationRepository.findAll().get(0);
        Roster roster = rosterRepository.findAll().get(0);
        roster.setFromDateTime(roster.getFromDateTime().plusHours(1));
        URI uri = new URI(baseUrl + port + "/work-locations/" + workLocation.getId() + "/rosters/" + roster.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<Roster> entity = new HttpEntity<Roster>(roster, headers);
        ResponseEntity<Roster> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, Roster.class);

        RosterNotFoundException e = new RosterNotFoundException(roster.getId(), workLocation.getId());

        assertEquals(roster.getFromDateTime(), result.getBody().getFromDateTime());
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void deleteRoster_WorkLocationDoesNotExist_Return404() throws URISyntaxException {
        WorkLocation workLocation = new WorkLocation();
        workLocation.setId(UUID.randomUUID());
        Roster roster = new Roster();
        roster.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/work-locations/" + workLocation.getId() + "/rosters/" + roster.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);

        WorkLocationNotFoundException e = new WorkLocationNotFoundException(workLocation.getId());

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteRoster_RosterDoesNotExist_Return404() throws URISyntaxException {
        WorkLocation workLocation = workLocationRepository.findAll().get(0);
        Roster roster = new Roster();
        roster.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/work-locations/" + workLocation.getId() + "/rosters/" + roster.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);

        RosterNotFoundException e = new RosterNotFoundException(roster.getId(), workLocation.getId());

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteRoster_RosterExists_Return200() throws URISyntaxException {
        WorkLocation workLocation = workLocationRepository.findAll().get(0);
        Roster roster = rosterRepository.findAll().get(0);
        URI uri = new URI(baseUrl + port + "/work-locations/" + workLocation.getId() + "/rosters/" + roster.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<Roster> result = restTemplate.exchange(uri, HttpMethod.DELETE, entity, Roster.class);


        assertEquals(200, result.getStatusCode().value());
    }
}
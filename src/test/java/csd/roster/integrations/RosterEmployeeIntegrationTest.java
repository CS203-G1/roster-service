package csd.roster.integrations;

import csd.roster.RosterServiceApplication;
import csd.roster.configurations.IntegrationTestConfig;
import csd.roster.domain.enumerator.HealthStatus;
import csd.roster.domain.exception.exceptions.*;
import csd.roster.domain.model.*;
import csd.roster.repo.repository.*;
import csd.roster.repo.util.AwsCognitoUtil;
import csd.roster.services.service.interfaces.EmployeeService;
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
import wiremock.org.apache.commons.lang3.RandomStringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = { RosterServiceApplication.class, IntegrationTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class RosterEmployeeIntegrationTest {
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
    private RosterEmployeeRepository rosterEmployeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AwsCognitoUtil awsCognitoUtil;

    private String accessToken;


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

        // Initialise roster with 4 employees
        for(int i = 0 ; i < 4; i++){
            Employee employee = new Employee();
            employee.setName("Test Employee " + i);
            employee.setEmail(randomEmail());
            Employee savedEmployee = employeeService.addEmployee(savedDepartment.getId(), employee);
            employeeService.addEmployeeToWorkLocation(savedWorkLocation.getId(), savedEmployee.getId());

            RosterEmployee rosterEmployee = new RosterEmployee();
            rosterEmployee.setEmployee(savedEmployee);
            rosterEmployee.setRoster(savedRoster);
            rosterEmployee.setRemote(true);
            RosterEmployee savedRosterEmployee = rosterEmployeeRepository.save(rosterEmployee);
        }

    }

    @AfterEach
    public void tearDown(){
        rosterEmployeeRepository.deleteAll();
        for(Employee emp : employeeService.getAllEmployees()){
            awsCognitoUtil.deleteUser(emp.getId().toString());
            employeeService.deleteEmployee(emp.getDepartment().getId(), emp.getId());
        }
        rosterRepository.deleteAll();
        workLocationRepository.deleteAll();
        departmentRepository.deleteAll();
        companyRepository.deleteAll();
    }

    private String randomEmail(){
        String prefix = RandomStringUtils.randomAlphabetic(8);
        return prefix + "@test.com";
    }

    @Test
    public void addRosterEmployee_RosterDoesNotExist_Return404() throws URISyntaxException {
        Roster roster = new Roster();
        roster.setId(UUID.randomUUID());
        Employee employee= new Employee();
        employee.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port +  "/rosters/" + roster.getId() + "/employees/" + employee.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        RosterNotFoundException e = new RosterNotFoundException(roster.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void addRosterEmployee_EmployeeDoesNotExist_Return404() throws URISyntaxException {
        Roster roster = rosterRepository.findAll().get(0);
        Employee employee= new Employee();
        employee.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port +  "/rosters/" + roster.getId() + "/employees/" + employee.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        EmployeeNotFoundException e = new EmployeeNotFoundException(employee.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void addRosterEmployee_DuplicateRosterEmployee_Return422() throws URISyntaxException {
        Roster roster = rosterRepository.findAll().get(0);
        Employee employee= employeeService.getAllEmployees().get(0);
        URI uri = new URI(baseUrl + port +  "/rosters/" + roster.getId() + "/employees/" + employee.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        assertEquals(422, result.getStatusCode().value());
    }

    @Test
    public void addRosterEmployee_EmployeeUnhealthy_Return422() throws URISyntaxException {
        Roster roster = rosterRepository.findAll().get(0);
        Department department = departmentRepository.findAll().get(0);
        WorkLocation workLocation = workLocationRepository.findAll().get(0);
        Employee employee= new Employee();
        employee.setHealthStatus(HealthStatus.COVID);
        employee.setEmail(randomEmail());
        employee = employeeService.addEmployee(department.getId(), employee);
        employeeService.addEmployeeToWorkLocation(workLocation.getId(),employee.getId());
        URI uri = new URI(baseUrl + port +  "/rosters/" + roster.getId() + "/employees/" + employee.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        EmployeeNotHealthyException e = new EmployeeNotHealthyException(employee);

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(422, result.getStatusCode().value());
    }

    @Test
    public void addRosterEmployee_RosterEmployeeAdded_Return200() throws URISyntaxException {
        Roster roster = rosterRepository.findAll().get(0);
        Department department = departmentRepository.findAll().get(0);
        WorkLocation workLocation = workLocationRepository.findAll().get(0);
        Employee employee= new Employee();
        employee.setHealthStatus(HealthStatus.HEALTHY);
        employee.setEmail(randomEmail());
        employee = employeeService.addEmployee(department.getId(), employee);
        employeeService.addEmployeeToWorkLocation(workLocation.getId(),employee.getId());
        URI uri = new URI(baseUrl + port +  "/rosters/" + roster.getId() + "/employees/" + employee.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<RosterEmployee> result = restTemplate.exchange(uri, HttpMethod.POST, entity, RosterEmployee.class);


        assertEquals(200, result.getStatusCode().value());
    }


    @Test
    public void updateRosterEmployee_RosterDoesNotExist_Return404() throws URISyntaxException {
        Roster roster = new Roster();
        roster.setId(UUID.randomUUID());
        Employee employee= new Employee();
        employee.setId(UUID.randomUUID());
        RosterEmployee rosterEmployee = new RosterEmployee();
        rosterEmployee.setEmployee(employee);
        rosterEmployee.setRoster(roster);
        rosterEmployee.setRemote(false);
        URI uri = new URI(baseUrl + port +  "/rosters/" + roster.getId() + "/employees/" + employee.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<RosterEmployee> entity = new HttpEntity<RosterEmployee>(rosterEmployee, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);

        RosterNotFoundException e = new RosterNotFoundException(roster.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void updateRosterEmployee_EmployeeDoesNotExist_Return404() throws URISyntaxException {
        Roster roster = rosterRepository.findAll().get(0);
        Employee employee= new Employee();
        employee.setId(UUID.randomUUID());
        RosterEmployee rosterEmployee = new RosterEmployee();
        rosterEmployee.setEmployee(employee);
        rosterEmployee.setRoster(roster);
        rosterEmployee.setRemote(false);
        URI uri = new URI(baseUrl + port +  "/rosters/" + roster.getId() + "/employees/" + employee.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<RosterEmployee> entity = new HttpEntity<RosterEmployee>(rosterEmployee, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);

        EmployeeNotFoundException e = new EmployeeNotFoundException(employee.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void updateRosterEmployee_RosterEmployeeUpdated_Return200() throws URISyntaxException {
        Roster roster = rosterRepository.findAll().get(0);
        Employee employee= employeeService.getAllEmployees().get(0);
        RosterEmployee rosterEmployee = new RosterEmployee();
        rosterEmployee.setEmployee(employee);
        rosterEmployee.setRoster(roster);
        rosterEmployee.setRemote(false);
        URI uri = new URI(baseUrl + port +  "/rosters/" + roster.getId() + "/employees/" + employee.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<RosterEmployee> entity = new HttpEntity<RosterEmployee>(rosterEmployee, headers);
        ResponseEntity<RosterEmployee> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, RosterEmployee.class);


        assertEquals( false, result.getBody().isRemote());
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void deleteRosterEmployee_RosterDoesNotExist_Return404() throws URISyntaxException {
        Roster roster = new Roster();
        roster.setId(UUID.randomUUID());
        Employee employee= new Employee();
        employee.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port +  "/rosters/" + roster.getId() + "/employees/" + employee.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);

        RosterNotFoundException e = new RosterNotFoundException(roster.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteRosterEmployee_EmployeeDoesNotExist_Return404() throws URISyntaxException {
        Roster roster = rosterRepository.findAll().get(0);
        Employee employee= new Employee();
        employee.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port +  "/rosters/" + roster.getId() + "/employees/" + employee.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);

        EmployeeNotFoundException e = new EmployeeNotFoundException(employee.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteRosterEmployee_RosterEmployeeDeleted_Return200() throws URISyntaxException {
        Roster roster = rosterRepository.findAll().get(0);
        Employee employee= employeeService.getAllEmployees().get(0);
        URI uri = new URI(baseUrl + port +  "/rosters/" + roster.getId() + "/employees/" + employee.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);

        assertEquals(200, result.getStatusCode().value());
    }
}

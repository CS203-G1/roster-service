package csd.roster.integrations;

import csd.roster.RosterServiceApplication;
import csd.roster.configurations.IntegrationTestConfig;
import csd.roster.domain.enumerator.HealthStatus;
import csd.roster.domain.enumerator.VaccinationBrand;
import csd.roster.domain.enumerator.VaccinationStatus;
import csd.roster.domain.exception.exceptions.CompanyNotFoundException;
import csd.roster.domain.exception.exceptions.EmployeeNotFoundException;
import csd.roster.domain.model.*;
import csd.roster.domain.response_model.SummaryResponseModel;
import csd.roster.repo.repository.*;
import csd.roster.repo.util.AwsCognitoUtil;
import csd.roster.repo.util.AwsS3Util;
import csd.roster.services.service.interfaces.EmployeeLogService;
import csd.roster.services.service.interfaces.EmployeeService;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkStatisticsIntegrationTest {
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
    private EmployeeLogRepository employeeLogRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeLogService employeeLogService;

    @Autowired
    private AwsCognitoUtil awsCognitoUtil;

    private String accessToken;


    @BeforeAll
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

        final int MOCK_EMPLOYEE_COUNT = 6;
        for(int i = 0; i < MOCK_EMPLOYEE_COUNT; i++){
            HealthStatus healthStatus = HealthStatus.HEALTHY;
            VaccinationStatus vaccinationStatus = VaccinationStatus.SECOND_DOSE;
            VaccinationBrand vaccinationBrand = VaccinationBrand.PFIZER;
            Employee employee = new Employee();
            employee.setName("Test Employee " + i);
            employee.setEmail(randomEmail());
            employee.setVaccinationBrand(vaccinationBrand);
            employee.setVaccinationStatus(vaccinationStatus);
            employee.setHealthStatus(healthStatus);
            employee.setIsInCompany(true);
            Employee savedEmployee = employeeService.addEmployee(savedDepartment.getId(), employee);
            employeeService.addEmployeeToWorkLocation(savedWorkLocation.getId(), employee.getId());
            employeeLogService.saveEmployeeLog(employee);


            RosterEmployee rosterEmployee = new RosterEmployee();
            rosterEmployee.setEmployee(savedEmployee);
            rosterEmployee.setRoster(savedRoster);
            // Set half the employees to be working remotely
            rosterEmployee.setRemote(i < ((MOCK_EMPLOYEE_COUNT + 1)/ 2));
            RosterEmployee savedRosterEmployee = rosterEmployeeRepository.save(rosterEmployee);

        }

    }

    @AfterAll
    public void tearDown(){
        rosterEmployeeRepository.deleteAll();
        employeeLogRepository.deleteAll();

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
    public void getDailyWorkStatisticsByCompany_CompanyNotFound_Return404() throws URISyntaxException {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/work-statistics/daily");

        // Passing Cognito jwt token into headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        CompanyNotFoundException e = new CompanyNotFoundException(company.getId());

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getDailyWorkStatisticsByCompany_CompanyExists_Return200() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/work-statistics/daily");

        // Passing Cognito jwt token into headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        // Result will be null due to constraints on adding employee logs

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void getOnsiteEmployeesListByCompanyAndDate_CompanyNotFound_Return404() throws URISyntaxException {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/work-statistics/onsite");

        // Passing Cognito jwt token into headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        CompanyNotFoundException e = new CompanyNotFoundException(company.getId());

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getOnsiteEmployeesListByCompanyAndDate_CompanyExists_Return200() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/work-statistics/onsite");

        // Passing Cognito jwt token into headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        // Result will be null due to constraints on adding employee logs

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void getWeeklyWorkStatisticsByCompany_CompanyNotFound_Return404() throws URISyntaxException {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/work-statistics/weekly");

        // Passing Cognito jwt token into headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        CompanyNotFoundException e = new CompanyNotFoundException(company.getId());

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getWeeklyWorkStatisticsByCompany_CompanyExists_Return200() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/work-statistics/weekly");

        // Passing Cognito jwt token into headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        // Result will be null due to constraints on adding employee logs

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void getDailySummaryByCompany_CompanyNotFound_Return404() throws URISyntaxException {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/work-statistics/summary");

        // Passing Cognito jwt token into headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        CompanyNotFoundException e = new CompanyNotFoundException(company.getId());

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getDailySummaryByCompany_CompanyExists_Return200() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);
        URI uri = new URI(baseUrl + port + "/companies/" + company.getId() + "/work-statistics/summary");

        // Passing Cognito jwt token into headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<SummaryResponseModel> result = restTemplate.exchange(uri, HttpMethod.GET, entity, SummaryResponseModel.class);
        // Result will be null due to constraints on adding employee logs

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void getDailySummaryByEmployersCompany_EmployerNotFound_Return404() throws URISyntaxException {
        Employee employer = new Employee();
        employer.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port + "/employees/" + employer.getId() + "/work-statistics/summary");

        // Passing Cognito jwt token into headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        EmployeeNotFoundException e = new EmployeeNotFoundException(employer.getId());

        assertEquals(e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getDailySummaryByEmployersCompany_EmployerExists_Return200() throws URISyntaxException {
        Employee employer = employeeService.getAllEmployees().get(0);
        URI uri = new URI(baseUrl + port + "/employees/" + employer.getId() + "/work-statistics/summary");

        // Passing Cognito jwt token into headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        // Result will be null due to constraints on adding employee logs

        assertEquals(200, result.getStatusCode().value());
    }
}

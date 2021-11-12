package csd.roster.integrations;

import csd.roster.RosterServiceApplication;
import csd.roster.configurations.IntegrationTestConfig;
import csd.roster.domain.exception.exceptions.CompanyNotFoundException;
import csd.roster.domain.exception.exceptions.DepartmentNotFoundException;
import csd.roster.domain.exception.exceptions.EmployeeNotFoundException;
import csd.roster.domain.model.Company;
import csd.roster.domain.model.Department;
import csd.roster.domain.model.Employee;
import csd.roster.repo.repository.CompanyRepository;
import csd.roster.repo.repository.DepartmentRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = { RosterServiceApplication.class, IntegrationTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class EmployeeIntegrationTest {
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

        Employee employee = new Employee();
        employee.setName("Test Employee 1");
        employee.setEmail(randomEmail());
        Employee savedEmployee = employeeService.addEmployee(savedDepartment.getId(), employee);

    }

    @AfterEach
    public void tearDown(){
        for(Employee emp : employeeService.getAllEmployees()){
            awsCognitoUtil.deleteUser(emp.getId().toString());
            employeeService.deleteEmployee(emp.getDepartment().getId(), emp.getId());
        }

        departmentRepository.deleteAll();
        companyRepository.deleteAll();
    }

    private String randomEmail(){
        String prefix = RandomStringUtils.randomAlphabetic(8);
        return prefix + "@test.com";
    }


    @Test
    public void getEmployee_DepartmentDoesNotExist_Return404() throws URISyntaxException {
        Department department = new Department();
        department.setId(UUID.randomUUID());
        Employee employee= new Employee();
        employee.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port +  "/departments/" + department.getId() + "/employees/" + employee.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        DepartmentNotFoundException e = new DepartmentNotFoundException(department.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getEmployee_EmployeeDoesNotExist_Return404() throws URISyntaxException {
        Department department = departmentRepository.findAll().get(0);
        Employee employee= new Employee();
        employee.setId(UUID.randomUUID());
        URI uri = new URI(baseUrl + port +  "/departments/" + department.getId() + "/employees/" + employee.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);


        EmployeeNotFoundException e = new EmployeeNotFoundException(employee.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getEmployee_EmployeeExists_Return200() throws URISyntaxException {
        Department department = departmentRepository.findAll().get(0);
        Employee employee= employeeService.getAllEmployees().get(0);
        URI uri = new URI(baseUrl + port +  "/departments/" + department.getId() + "/employees/" + employee.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<Employee> result = restTemplate.exchange(uri, HttpMethod.GET, entity, Employee.class);


        assertEquals(employee.getId(), result.getBody().getId());
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void addEmployee_DepartmentDoesNotExist_Return404() throws URISyntaxException {
        Department department = new Department();
        department.setId(UUID.randomUUID());
        Employee employee= new Employee();
        employee.setName("Added Employee");
        employee.setDepartment(department);
        employee.setEmail(randomEmail());
        URI uri = new URI(baseUrl + port +  "/departments/" + department.getId() + "/employees/");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<Employee> entity = new HttpEntity<Employee>(employee, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        DepartmentNotFoundException e = new DepartmentNotFoundException(department.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void addEmployee_EmployeeAdded_Return200() throws URISyntaxException {
        Department department = departmentRepository.findAll().get(0);
        Employee employee= new Employee();
        employee.setName("Added Employee");
        employee.setDepartment(department);
        employee.setEmail(randomEmail());
        URI uri = new URI(baseUrl + port +  "/departments/" + department.getId() + "/employees/");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<Employee> entity = new HttpEntity<Employee>(employee, headers);
        ResponseEntity<Employee> result = restTemplate.exchange(uri, HttpMethod.POST, entity, Employee.class);


        assertEquals(employee.getName(), result.getBody().getName());
        assertEquals(200, result.getStatusCode().value());
    }
}

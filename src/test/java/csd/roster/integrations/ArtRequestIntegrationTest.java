package csd.roster.integrations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import csd.roster.RosterServiceApplication;
import csd.roster.configurations.IntegrationTestConfig;
import csd.roster.domain.enumerator.HealthStatus;
import csd.roster.domain.enumerator.RequestStatus;
import csd.roster.domain.exception.exceptions.ArtRequestNotFoundException;
import csd.roster.domain.exception.exceptions.CompanyNotFoundException;
import csd.roster.domain.exception.exceptions.EmployeeNotFoundException;
import csd.roster.domain.holder.ArtRequestReviewHolder;
import csd.roster.domain.model.ArtRequest;
import csd.roster.domain.model.Company;
import csd.roster.domain.model.Department;
import csd.roster.domain.model.Employee;
import csd.roster.repo.repository.ArtRequestRepository;
import csd.roster.repo.repository.CompanyRepository;
import csd.roster.repo.repository.DepartmentRepository;
import csd.roster.repo.util.AwsCognitoUtil;
import csd.roster.repo.util.AwsS3Util;
import csd.roster.services.service.interfaces.EmployeeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import wiremock.org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = { RosterServiceApplication.class, IntegrationTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ArtRequestIntegrationTest {
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
    private ArtRequestRepository artRequestRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AwsCognitoUtil awsCognitoUtil;

    @Autowired
    private AwsS3Util awsS3Util;

    @Value(value = "${aws.s3-bucket}")
    private String s3bucket;

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

        ArtRequest artRequest = new ArtRequest();
        artRequest.setEmployee(savedEmployee);
        ArtRequest savedArtRequest = artRequestRepository.save(artRequest);

    }

    @AfterEach
    public void tearDown(){
        for(ArtRequest artRequest : artRequestRepository.findAll()){
            awsS3Util.delete(s3bucket, artRequest.getFilePath());
            artRequestRepository.delete(artRequest);
        }

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
    public void getArtRequestByCompany_CompanyDoesNotExist_Return404() throws URISyntaxException {
        Company company = new Company();
        company.setId(UUID.randomUUID());

        URI uri = new URI(baseUrl + port +  "/companies/" + company.getId() + "/requests/art-request/" + RequestStatus.PENDING);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        CompanyNotFoundException e = new CompanyNotFoundException(company.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getArtRequestByEmployee_EmployeeDoesNotExist_Return404() throws URISyntaxException {
        Employee employee = new Employee();
        employee.setId(UUID.randomUUID());

        URI uri = new URI(baseUrl + port +  "/employees/" + employee.getId() + "/requests/art-request/" + RequestStatus.PENDING);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        EmployeeNotFoundException e = new EmployeeNotFoundException(employee.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getArtRequestByCompany_ArtRequestExist_Return200() throws URISyntaxException {
        Company company = companyRepository.findAll().get(0);

        URI uri = new URI(baseUrl + port +  "/companies/" + company.getId() + "/requests/art-request/" + RequestStatus.PENDING);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);


        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void getArtRequestByEmployee_ArtRequestExist_Return200() throws URISyntaxException {
        Employee employee = employeeService.getAllEmployees().get(0);

        URI uri = new URI(baseUrl + port +  "/employees/" + employee.getId() + "/requests/art-request/" + RequestStatus.PENDING);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);


        assertEquals(200, result.getStatusCode().value());
    }

// Commented off until multipartfile can be mocked
//    @Test
//    public void addArtRequest_EmployeeDoesNotExist_Return404() throws URISyntaxException, IOException {
//        Employee employee = new Employee();
//        employee.setId(UUID.randomUUID());
//        MultipartFile multipartFile = null;
//
//
//        URI uri = new URI(baseUrl + port +  "/employees/" + employee.getId() + "/requests/art-request/");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + this.accessToken);
//
//        HttpEntity<MultipartFile> entity = new HttpEntity<MultipartFile>(multipartFile, headers);
//        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
//
//        EmployeeNotFoundException e = new EmployeeNotFoundException(employee.getId());
//
//        assertEquals( e.getMessage(), result.getBody());
//        assertEquals(404, result.getStatusCode().value());
//    }
//
//    @Test
//    public void addArtRequest_ArtRequestAdded_Return200() throws URISyntaxException, IOException {
//        Employee employee = employeeService.getAllEmployees().get(0);
//        MultipartFile multipartFile = null;
//
//        URI uri = new URI(baseUrl + port +  "/employees/" + employee.getId() + "/requests/art-request/");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + this.accessToken);
//
//        HttpEntity<MultipartFile> entity = new HttpEntity<MultipartFile>(multipartFile, headers);
//        ResponseEntity<ArtRequest> result = restTemplate.exchange(uri, HttpMethod.POST, entity, ArtRequest.class);
//
//
//        assertEquals(200, result.getStatusCode().value());
//    }

        @Test
        public void reviewArtRequest_ArtRequestDoesNotExist_Return404() throws URISyntaxException, IOException {
        ArtRequest artRequest = new ArtRequest();
        artRequest.setId(UUID.randomUUID());
        ArtRequestReviewHolder artRequestReviewHolder = new ArtRequestReviewHolder();
        artRequestReviewHolder.setRequestStatus(RequestStatus.APPROVED);
        artRequestReviewHolder.setHealthStatus(HealthStatus.COVID);

        URI uri = new URI(baseUrl + port +  "/requests/art-request/" + artRequest.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<ArtRequestReviewHolder> entity = new HttpEntity<ArtRequestReviewHolder>(artRequestReviewHolder, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);

        ArtRequestNotFoundException e = new ArtRequestNotFoundException(artRequest.getId());

        assertEquals( e.getMessage(), result.getBody());
        assertEquals(404, result.getStatusCode().value());
    }

        @Test
        public void reviewArtRequest_ArtRequestApproved_Return200() throws URISyntaxException, IOException {
        Employee employee = employeeService.getAllEmployees().get(0);
        ArtRequest artRequest = artRequestRepository.findAll().get(0);
        ArtRequestReviewHolder artRequestReviewHolder = new ArtRequestReviewHolder();
        artRequestReviewHolder.setRequestStatus(RequestStatus.APPROVED);
        artRequestReviewHolder.setHealthStatus(HealthStatus.COVID);

        URI uri = new URI(baseUrl + port +  "/requests/art-request/" + artRequest.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);

        HttpEntity<ArtRequestReviewHolder> entity = new HttpEntity<ArtRequestReviewHolder>(artRequestReviewHolder, headers);
        ResponseEntity<ArtRequest> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, ArtRequest.class);


        assertEquals(HealthStatus.COVID, result.getBody().getEmployee().getHealthStatus());
        assertEquals(200, result.getStatusCode().value());
    }
}

package csd.roster.integrations;

import csd.roster.RosterServiceApplication;
import csd.roster.configurations.IntegrationTestConfig;
import csd.roster.model.Company;
import csd.roster.model.Department;
import csd.roster.model.Roster;
import csd.roster.model.WorkLocation;
import csd.roster.repository.CompanyRepository;
import csd.roster.repository.DepartmentRepository;
import csd.roster.repository.RosterRepository;
import csd.roster.repository.WorkLocationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

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

        Roster roster = new Roster();
        roster.setWorkLocation(savedWorkLocation);
        roster.setDate(LocalDate.now());
        Roster savedRoster = rosterRepository.save(roster);
    }

    @AfterEach
    public void tearDown(){
        rosterRepository.deleteAll();
        workLocationRepository.deleteAll();
        departmentRepository.deleteAll();
        companyRepository.deleteAll();
    }
}

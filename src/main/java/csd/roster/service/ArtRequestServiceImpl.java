package csd.roster.service;

import csd.roster.enumerator.HealthStatus;
import csd.roster.enumerator.RequestStatus;
import csd.roster.exception.exceptions.ArtRequestNotFoundException;
import csd.roster.model.ArtRequest;
import csd.roster.model.Employee;
import csd.roster.repository.ArtRequestRepository;
import csd.roster.service.interfaces.ArtRequestService;
import csd.roster.service.interfaces.EmployeeService;
import csd.roster.util.AwsS3Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ArtRequestServiceImpl implements ArtRequestService {
    private final ArtRequestRepository artRequestRepository;

    private final EmployeeService employeeService;

    private final AwsS3Util awsS3Util;

    @Value(value = "${aws.s3-bucket}")
    private String s3bucket;

    @Autowired
    public ArtRequestServiceImpl(
            ArtRequestRepository artRequestRepository,
            EmployeeService employeeService,
            AwsS3Util awsS3Util) {
        this.artRequestRepository = artRequestRepository;
        this.employeeService = employeeService;
        this.awsS3Util = awsS3Util;
    }

    @Override
    public ArtRequest addArtRequest(final UUID employeeId, final MultipartFile multipartFile) {
        Employee employee = employeeService.getEmployee(employeeId);
        ArtRequest artRequest= new ArtRequest();
        artRequest.setEmployee(employee);
        try {
            File file = File.createTempFile("art_request", ".jpg");
            convertMultipartFileToFile(multipartFile, file);
            URL imageUrl = awsS3Util.upload(s3bucket, artRequest.getFilePath(), file);
            artRequest.setImageUrl(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return artRequestRepository.save(artRequest);
    }

    @Override
    public ArtRequest getArtRequest(final UUID id) {
        return artRequestRepository.findById(id)
                .orElseThrow(() -> new ArtRequestNotFoundException(id));
    }

    @Override
    public List<ArtRequest> getArtRequestByEmployeeIdAndRequestStatus(final UUID employeeId,
                                                                      final RequestStatus requestStatus){
        employeeService.getEmployee(employeeId);

        return artRequestRepository.findAllByEmployeeIdAndRequestStatus(employeeId, requestStatus);
    }

    @Override
    public List<ArtRequest> getArtRequestsByCompanyIdAndApprovalStatus(final UUID companyId,
                                                                       final RequestStatus requestStatus){
        List<Employee> employees = employeeService.getAllEmployeesByCompanyId(companyId);

        ArrayList<ArtRequest> artRequests = new ArrayList<ArtRequest>();
        for(Employee employee: employees){
            List<ArtRequest> employeeRequests = getArtRequestByEmployeeIdAndRequestStatus(employee.getId(), requestStatus);
            artRequests.addAll(employeeRequests);
        }
        return artRequests;
    }

    @Override
    public ArtRequest reviewArtRequest(final UUID id,
                                       final HealthStatus healthStatus,
                                       final RequestStatus requestStatus) {
        ArtRequest artRequest = getArtRequest(id);
        artRequest.setHealthStatus(healthStatus);
        artRequest.setRequestStatus(requestStatus);
        if(requestStatus == RequestStatus.APPROVED){
            Employee employee = artRequest.getEmployee();
            employee.setHealthStatus(healthStatus);
            employeeService.updateEmployee(employee.getDepartment().getId(), employee.getId(), employee);
        }
        return artRequestRepository.save(artRequest);
    }

    private void convertMultipartFileToFile(final MultipartFile multipartFile,
                                            final File file) {
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

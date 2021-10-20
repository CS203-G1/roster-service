package csd.roster.service;

import csd.roster.enumerator.HealthStatus;
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Service
public class ArtRequestServiceImpl implements ArtRequestService {
    private ArtRequestRepository artRequestRepository;

    private EmployeeService employeeService;

    private AwsS3Util awsS3Util;

    @Value(value = "${aws.s3-bucket}")
    private String s3bucket;

    @Autowired
    public ArtRequestServiceImpl(ArtRequestRepository artRequestRepository, EmployeeService employeeService, AwsS3Util awsS3Util) {
        this.artRequestRepository = artRequestRepository;
        this.employeeService = employeeService;
        this.awsS3Util = awsS3Util;
    }

    @Override
    public ArtRequest addArtRequest(UUID employeeId, MultipartFile multipartFile) {
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
    public ArtRequest getArtRequest(UUID id) {
        return null;
    }

    @Override
    public ArtRequest reviewArtRequest(UUID id, HealthStatus healthStatus) {
        return null;
    }

    private void convertMultipartFileToFile(MultipartFile multipartFile, File file) {
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

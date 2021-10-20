package csd.roster.controller;

import csd.roster.model.ArtRequest;
import csd.roster.service.ArtRequestServiceImpl;
import csd.roster.service.interfaces.ArtRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
public class ArtRequestController {
    private ArtRequestService artRequestService;

    @Autowired
    public ArtRequestController(ArtRequestServiceImpl artRequestService){
        this.artRequestService = artRequestService;
    }

    @PostMapping("/employees/{employeeId}/requests/art-request")
    public ArtRequest addArtRequest(@PathVariable(value = "employeeId") UUID employeeId, @RequestParam("file") MultipartFile multipartFile){
        return artRequestService.addArtRequest(employeeId, multipartFile);
    }
}

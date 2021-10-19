package csd.roster.controller;

import csd.roster.enumerator.HealthStatus;
import csd.roster.enumerator.RequestStatus;
import csd.roster.holder.ArtRequestReviewHolder;
import csd.roster.model.ArtRequest;
import csd.roster.service.ArtRequestServiceImpl;
import csd.roster.service.interfaces.ArtRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@PreAuthorize("hasRole('ROLE_EMPLOYER')")
@RestController
public class ArtRequestController {
    private ArtRequestService artRequestService;

    @Autowired
    public ArtRequestController(ArtRequestServiceImpl artRequestService){
        this.artRequestService = artRequestService;
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping("/employees/{employeeId}/requests/art-request")
    public ArtRequest addArtRequest(@PathVariable(value = "employeeId") UUID employeeId, @RequestParam("file") MultipartFile multipartFile){
        return artRequestService.addArtRequest(employeeId, multipartFile);
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    @GetMapping("/employees/{employeeId}/requests/art-request/{requestStatus}")
    public List<ArtRequest> getArtRequestsByEmployeeAndRequestStatus(@PathVariable(value = "employeeId") UUID employeeId,
                                                                     @PathVariable(value = "requestStatus")RequestStatus requestStatus){
        return artRequestService.getArtRequestByEmployeeIdAndRequestStatus(employeeId,requestStatus);
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    @GetMapping("/companies/{companyId}/requests/art-request/{requestStatus}")
    public List<ArtRequest> getArtRequestsByCompanyAndRequestStatus(@PathVariable(value = "companyId") UUID companyId,
                                                                    @PathVariable(value = "requestStatus")RequestStatus requestStatus){
        return artRequestService.getArtRequestsByCompanyIdAndApprovalStatus(companyId,requestStatus);
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    @PutMapping("/requests/art-request/{artRequestId}")
    public ArtRequest reviewArtRequest(@PathVariable(value = "artRequestId") UUID artRequestId,
                                       @RequestBody ArtRequestReviewHolder artRequestReviewHolder){
        return artRequestService.reviewArtRequest(artRequestId, artRequestReviewHolder.getHealthStatus(), artRequestReviewHolder.getRequestStatus());
    }
}
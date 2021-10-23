package csd.roster.service.interfaces;

import csd.roster.enumerator.HealthStatus;
import csd.roster.enumerator.RequestStatus;
import csd.roster.model.ArtRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtRequestService {
    ArtRequest addArtRequest(UUID employeeId, MultipartFile multipartFile);

    ArtRequest getArtRequest(UUID id);

    List<ArtRequest> getArtRequestByEmployeeIdAndRequestStatus(UUID employeeId, RequestStatus requestStatus);

    List<ArtRequest> getArtRequestsByCompanyIdAndApprovalStatus(UUID companyId, RequestStatus requestStatus);

    ArtRequest reviewArtRequest(UUID id, HealthStatus healthStatus, RequestStatus requestStatus);


}

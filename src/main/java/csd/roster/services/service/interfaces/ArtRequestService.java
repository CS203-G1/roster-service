package csd.roster.services.service.interfaces;

import csd.roster.domain.enumerator.HealthStatus;
import csd.roster.domain.enumerator.RequestStatus;
import csd.roster.domain.model.ArtRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ArtRequestService {
    ArtRequest addArtRequest(UUID employeeId, MultipartFile multipartFile);

    ArtRequest getArtRequest(UUID id);

    List<ArtRequest> getArtRequestByEmployeeIdAndRequestStatus(UUID employeeId, RequestStatus requestStatus);

    List<ArtRequest> getArtRequestsByCompanyIdAndApprovalStatus(UUID companyId, RequestStatus requestStatus);

    ArtRequest reviewArtRequest(UUID id, HealthStatus healthStatus, RequestStatus requestStatus);


}

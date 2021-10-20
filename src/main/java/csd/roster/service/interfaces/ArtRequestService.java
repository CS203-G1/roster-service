package csd.roster.service.interfaces;

import csd.roster.enumerator.HealthStatus;
import csd.roster.model.ArtRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

public interface ArtRequestService {
    ArtRequest addArtRequest(UUID employeeId, MultipartFile multipartFile);

    ArtRequest getArtRequest(UUID id);

    ArtRequest reviewArtRequest(UUID id, HealthStatus healthStatus);

}

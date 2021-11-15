package csd.roster.services.service.interfaces;

import java.util.List;
import java.util.UUID;

import csd.roster.domain.model.WorkLocation;

public interface WorkLocationService {
    WorkLocation add(UUID departmentId, WorkLocation workLocation);

    List<WorkLocation> getWorkLocationsFromDepartment(UUID departmentId);

    void delete(UUID departmentId, UUID workLocationId);

    WorkLocation update(UUID departmentId, UUID workLocationId, WorkLocation workLocation);

    WorkLocation get(UUID departmentId, UUID workLocationId);

    WorkLocation getWorkLocationById(UUID workLocationId);

    WorkLocation getRemoteWorkLocationByCompanyId(UUID companyId);

    List<WorkLocation> getWorkLocationsByCompanyId(UUID companyId);

    List<WorkLocation> getAllWorkLocations();
}

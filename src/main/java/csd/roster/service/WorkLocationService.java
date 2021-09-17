package csd.roster.service;

import java.util.List;
import java.util.UUID;

import csd.roster.model.WorkLocation;

public interface WorkLocationService {
    WorkLocation add(UUID companyId,UUID departmentId, WorkLocation workLocation);
    List<WorkLocation> getWorkLocationsFromDepartment(UUID departmentId);
    void delete(UUID companyId, UUID departmentId, UUID workLocationId);
    WorkLocation update(UUID companyId, UUID departmentId, UUID workLocationId, WorkLocation workLocation);
    WorkLocation get(UUID companyId, UUID departmentId, UUID workLocationId);
    WorkLocation getById(UUID workLocationId);
}

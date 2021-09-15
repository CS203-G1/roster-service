package csd.roster.service;

import csd.roster.model.Department;
import csd.roster.model.WorkLocation;
import org.hibernate.jdbc.Work;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkLocationService {
    WorkLocation add(UUID companyId,UUID departmentId, WorkLocation workLocation);

    List<WorkLocation> getWorkLocationsFromDepartment(UUID departmentId);

    void delete(UUID departmentId, UUID workLocationId);

    WorkLocation update(UUID companyId, UUID departmentId, UUID workLocationId, WorkLocation workLocation);

    WorkLocation get(UUID companyId, UUID departmentId, UUID workLocationId);
}

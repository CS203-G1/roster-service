package csd.roster.service;

import csd.roster.exception.CompanyNotFoundException;
import csd.roster.exception.DepartmentNotFoundException;
import csd.roster.model.Company;
import csd.roster.model.Department;
import csd.roster.model.WorkLocation;
import csd.roster.repository.DepartmentRepository;
import csd.roster.repository.WorkLocationRepository;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WorkLocationServiceImpl implements WorkLocationService {
    final private WorkLocationRepository workLocationRepository;
    final private DepartmentService departmentService;

    public WorkLocationServiceImpl(WorkLocationRepository workLocationRepository, DepartmentService departmentService) {
        this.workLocationRepository = workLocationRepository;
        this.departmentService = departmentService;
    }

    @Override
    public WorkLocation add(UUID companyId, UUID departmentId, WorkLocation workLocation) {
        // throw error
        Department department = departmentService.getDepartmentByIdAndCompanyId(companyId, departmentId);

        workLocation.setDepartment(department);
        return workLocationRepository.save(workLocation);
    }

    @Override
    public List<WorkLocation> getWorkLocationsFromDepartment(UUID departmentId) {
        return null;
    }

    @Override
    public void delete(UUID departmentId, UUID workLocationId) {

    }

    @Override
    public WorkLocation update(UUID departmentId, UUID workLocationId, WorkLocation workLocation) {
        return null;
    }

    @Override
    public WorkLocation getWorkLocationByIdAndDepartmentId(UUID departmentId, UUID workLocationId) {
        return null;
    }
}

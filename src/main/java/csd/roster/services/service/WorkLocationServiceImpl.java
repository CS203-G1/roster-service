package csd.roster.services.service;

import java.util.List;
import java.util.UUID;

import csd.roster.services.service.interfaces.DepartmentService;
import csd.roster.services.service.interfaces.WorkLocationService;
import org.springframework.stereotype.Service;

import csd.roster.domain.exception.exceptions.WorkLocationNotFoundException;
import csd.roster.domain.model.Department;
import csd.roster.domain.model.WorkLocation;
import csd.roster.repo.repository.WorkLocationRepository;

@Service
public class WorkLocationServiceImpl implements WorkLocationService {
    private final WorkLocationRepository workLocationRepository;
    private final DepartmentService departmentService;

    public WorkLocationServiceImpl(final WorkLocationRepository workLocationRepository,
                                   final DepartmentService departmentService) {
        this.workLocationRepository = workLocationRepository;
        this.departmentService = departmentService;
    }

    @Override
    public WorkLocation add(final UUID companyId,
                            final UUID departmentId,
                            final WorkLocation workLocation) {
        Department department = departmentService.getDepartmentByIdAndCompanyId(departmentId, companyId);

        workLocation.setDepartment(department);
        return workLocationRepository.save(workLocation);
    }

    @Override
    public List<WorkLocation> getAllWorkLocations() {
        return workLocationRepository.findAll();
    }

    @Override
    public List<WorkLocation> getWorkLocationsFromDepartment(final UUID departmentId) {
        return null;
    }

    @Override
    public void delete(final UUID companyId, final UUID departmentId, final UUID workLocationId) {
        WorkLocation workLocation = get(companyId, departmentId, workLocationId);

        workLocationRepository.delete(workLocation);
    }

    @Override
    public WorkLocation update(final UUID companyId,
                               final UUID departmentId,
                               final UUID workLocationId,
                               final WorkLocation newWorkLocation) {
        Department department = departmentService.getDepartmentByIdAndCompanyId(departmentId, companyId);

        return workLocationRepository.findByIdAndDepartmentId(workLocationId, departmentId).map(workLocation -> {
            newWorkLocation.setDepartment(department);
            newWorkLocation.setId(workLocationId);
            return workLocationRepository.save(newWorkLocation);
        }).orElseThrow(() -> new WorkLocationNotFoundException(departmentId, companyId, workLocationId));
    }

    @Override
    public WorkLocation get(final UUID companyId,
                            final UUID departmentId,
                            final UUID workLocationId) {
        departmentService.getDepartmentByIdAndCompanyId(departmentId, companyId);

        return workLocationRepository.findByIdAndDepartmentId(workLocationId, departmentId)
                .orElseThrow(() -> new WorkLocationNotFoundException(departmentId, companyId, workLocationId));
    }

    @Override
    public WorkLocation getWorkLocationById(final UUID workLocationId) {
        return workLocationRepository.findById(workLocationId)
                .orElseThrow(() -> new WorkLocationNotFoundException(workLocationId));
    }

    @Override
    public WorkLocation getRemoteWorkLocationByCompanyId(final UUID companyId) {
        return workLocationRepository.findRemoteWorkLocationByCompanyId(companyId)
                .orElseThrow(() -> new WorkLocationNotFoundException(companyId));
    }

    @Override
    public List<WorkLocation> getWorkLocationsByCompanyId(final UUID companyId) {
        return workLocationRepository.findAllByCompanyId(companyId);
    }
}

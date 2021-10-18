package csd.roster.service;

import java.util.List;
import java.util.UUID;

import csd.roster.service.interfaces.DepartmentService;
import csd.roster.service.interfaces.WorkLocationService;
import org.springframework.stereotype.Service;

import csd.roster.exception.WorkLocationNotFoundException;
import csd.roster.model.Department;
import csd.roster.model.WorkLocation;
import csd.roster.repository.WorkLocationRepository;

@Service
public class WorkLocationServiceImpl implements WorkLocationService {
    final private WorkLocationRepository workLocationRepository;
    final private DepartmentService departmentService;

    public WorkLocationServiceImpl(WorkLocationRepository workLocationRepository, DepartmentService departmentService) {
        this.workLocationRepository = workLocationRepository;
        this.departmentService = departmentService;
    }

    @Override
    public WorkLocation add(UUID companyId, UUID departmentId, WorkLocation workLocation) {
        Department department = departmentService.getDepartmentByIdAndCompanyId(departmentId, companyId);

        workLocation.setDepartment(department);
        return workLocationRepository.save(workLocation);
    }

    @Override
    public List<WorkLocation> getWorkLocationsFromDepartment(UUID departmentId) {
        return null;
    }

    @Override
    public void delete(UUID companyId, UUID departmentId, UUID workLocationId) {
        WorkLocation workLocation = get(companyId, departmentId, workLocationId);

        workLocationRepository.delete(workLocation);
    }

    @Override
    public WorkLocation update(UUID companyId, UUID departmentId, UUID workLocationId, WorkLocation newWorkLocation) {
        Department department = departmentService.getDepartmentByIdAndCompanyId(departmentId, companyId);

        return workLocationRepository.findByIdAndDepartmentId(workLocationId, departmentId).map(workLocation -> {
            newWorkLocation.setDepartment(department);
            newWorkLocation.setId(workLocationId);
            return workLocationRepository.save(newWorkLocation);
        }).orElseThrow(() -> new WorkLocationNotFoundException(departmentId, companyId, workLocationId));
    }

    @Override
    public WorkLocation get(UUID companyId, UUID departmentId, UUID workLocationId) {
        departmentService.getDepartmentByIdAndCompanyId(departmentId, companyId);

        return workLocationRepository.findByIdAndDepartmentId(workLocationId, departmentId)
                .orElseThrow(() -> new WorkLocationNotFoundException(departmentId, companyId, workLocationId));
    }

    @Override
    public WorkLocation getWorkLocationById(UUID workLocationId) {
        return workLocationRepository.findById(workLocationId)
                .orElseThrow(() -> new WorkLocationNotFoundException(workLocationId));
    }

    @Override
    public WorkLocation getRemoteWorkLocationByCompanyId(UUID companyId) {
        return workLocationRepository.findRemoteWorkLocationByCompanyId(companyId)
                .orElseThrow(() -> new WorkLocationNotFoundException(companyId));
    }

    @Override
    public List<WorkLocation> getWorkLocationsByCompanyId(UUID companyId) {
        return workLocationRepository.findAllByCompanyId(companyId);
    }
}

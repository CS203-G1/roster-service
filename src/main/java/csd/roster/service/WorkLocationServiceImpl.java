package csd.roster.service;

import csd.roster.exception.CompanyNotFoundException;
import csd.roster.exception.DepartmentNotFoundException;
import csd.roster.exception.WorkLocationNotFoundException;
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
        Department department = departmentService.getDepartmentByIdAndCompanyId(companyId, departmentId);

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
        Department department = departmentService.getDepartmentByIdAndCompanyId(companyId, departmentId);

        return workLocationRepository.findByIdAndDepartmentId(workLocationId, departmentId).map(workLocation -> {
            newWorkLocation.setDepartment(department);
            newWorkLocation.setId(workLocationId);
            return workLocationRepository.save(newWorkLocation);
        }).orElseThrow(() -> new WorkLocationNotFoundException(departmentId, companyId, workLocationId));
    }

    @Override
    public WorkLocation get(UUID companyId, UUID departmentId, UUID workLocationId) {
        departmentService.getDepartmentByIdAndCompanyId(companyId, departmentId);

        return workLocationRepository.findByIdAndDepartmentId(workLocationId, departmentId)
                .orElseThrow(() -> new WorkLocationNotFoundException(departmentId, companyId, workLocationId));
    }
}

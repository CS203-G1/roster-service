package csd.roster.service;

import csd.roster.exception.CompanyNotFoundException;
import csd.roster.exception.DepartmentNotFoundException;
import csd.roster.model.Company;
import csd.roster.model.Department;
import csd.roster.repository.DepartmentRepository;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private DepartmentRepository departmentRepository;
    private CompanyService companyService;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, CompanyService companyService) {
        this.departmentRepository = departmentRepository;
        this.companyService = companyService;
    }

    @Override
    public Department add(UUID companyId, Department department) {
        Company company = companyService.getCompanyById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        department.setCompany(company);
        return departmentRepository.save(department);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Optional<Department> getDepartmentById(UUID id) {
        throw new NotYetImplementedException();
    }

    @Override
    public Optional<Department> getDepartmentByIdAndCompanyId(UUID departmentId, UUID companyId) {
        return departmentRepository.findByIdAndCompanyId(departmentId, companyId);
    }

    @Override
    public Department updateCompanyById(UUID id) {
        throw new NotYetImplementedException();
    }

    @Override
    public void delete(UUID companyId, UUID departmentId) {
        if (companyService.getCompanyById(companyId) == null)
            throw new CompanyNotFoundException(companyId);

        Department department = getDepartmentByIdAndCompanyId(departmentId, companyId)
                .orElseThrow(() -> new DepartmentNotFoundException(companyId, departmentId));

        departmentRepository.delete(department);
    }

    @Override
    public Department update(UUID companyId, UUID departmentId, Department department) {
        Company company = companyService.getCompanyById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        return getDepartmentByIdAndCompanyId(departmentId, companyId).map(oldDepartment -> {
            department.setCompany(company);
            department.setId(departmentId);
            return departmentRepository.save(department);
        }).orElseThrow(() -> new DepartmentNotFoundException(companyId, departmentId));

    }
}

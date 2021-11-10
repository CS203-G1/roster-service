package csd.roster.services.service;

import csd.roster.domain.exception.exceptions.DepartmentNotFoundException;
import csd.roster.model.Company;
import csd.roster.model.Department;
import csd.roster.repo.repository.DepartmentRepository;
import csd.roster.services.service.interfaces.CompanyService;
import csd.roster.services.service.interfaces.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CompanyService companyService;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, CompanyService companyService) {
        this.departmentRepository = departmentRepository;
        this.companyService = companyService;
    }

    @Override
    public Department add(final UUID companyId, final Department department) {
        Company company = companyService.getCompanyById(companyId);

        department.setCompany(company);
        return departmentRepository.save(department);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentByIdAndCompanyId(final UUID departmentId, final UUID companyId) {
        companyService.getCompanyById(companyId);

        return departmentRepository.findByIdAndCompanyId(departmentId, companyId)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentId, companyId));
    }

    @Override
    public Department getDepartmentById(final UUID departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentId));
    }

    @Override
    public void delete(final UUID companyId, final UUID departmentId) {
        // Using getDepartmentByIdAndCompanyId for DRY purposes
        Department department = getDepartmentByIdAndCompanyId(departmentId, companyId);

        departmentRepository.delete(department);
    }

    @Override
    public Department update(final UUID companyId, final UUID departmentId, final Department department) {
        // Notice here we are using repeated code as getDepartmentByIdAndCompanyId
        // The rational is that we need to get the company object so we can pass it in the params of setCompany

        Company company = companyService.getCompanyById(companyId);

        return departmentRepository.findByIdAndCompanyId(departmentId, companyId).map(oldDepartment -> {
            department.setCompany(company);
            department.setId(departmentId);
            return departmentRepository.save(department);
        }).orElseThrow(() -> new DepartmentNotFoundException(companyId, departmentId));
    }
}

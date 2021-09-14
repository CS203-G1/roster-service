package csd.roster.service;

import csd.roster.model.Department;
import csd.roster.repository.DepartmentRepository;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Department addDepartment(Department department) {
        throw new NotYetImplementedException();
    }

    @Override
    public List<Department> getAllDepartments() {
        throw new NotYetImplementedException();
    }

    @Override
    public Department getDepartmentById(UUID id) {
        throw new NotYetImplementedException();
    }

    @Override
    public Department updateCompanyById(UUID id) {
        throw new NotYetImplementedException();
    }

    @Override
    public Department deleteDepartmentById(UUID id) {
        throw new NotYetImplementedException();
    }
}

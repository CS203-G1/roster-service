package csd.roster.service;

import csd.roster.model.Department;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentService {
    Department addDepartment(Department department);

    List<Department> getAllDepartments();

    Optional<Department> getDepartmentById(UUID id);

    Department updateCompanyById(UUID id);

    void delete(Department department);

    Optional<Department> getDepartmentByIdAndCompanyId(UUID departmentId, UUID companyId);
}

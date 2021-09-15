package csd.roster.service;

import csd.roster.model.Department;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentService {
    Department add(UUID companyId, Department department);

    List<Department> getAllDepartments();

    Optional<Department> getDepartmentById(UUID id);

    Department updateCompanyById(UUID id);

    void delete(UUID companyId, UUID departmentId);

    Department update(UUID companyId, UUID departmentId, Department department);

    Department getDepartmentByIdAndCompanyId(UUID departmentId, UUID companyId);
}

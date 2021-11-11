package csd.roster.services.service.interfaces;

import csd.roster.domain.model.Department;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    Department add(UUID companyId, Department department);

    List<Department> getAllDepartments();

    void delete(UUID companyId, UUID departmentId);

    Department update(UUID companyId, UUID departmentId, Department department);

    Department getDepartmentByIdAndCompanyId(UUID departmentId, UUID companyId);

    Department getDepartmentById(UUID departmentId);
}

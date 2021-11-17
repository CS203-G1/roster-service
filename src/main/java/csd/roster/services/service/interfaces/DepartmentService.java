package csd.roster.services.service.interfaces;

import csd.roster.domain.model.Department;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    Department addDepartment(UUID companyId, Department department);

    List<Department> getAllDepartments();

    void deleteDepartmentByIdAndDepartmentId(UUID companyId, UUID departmentId);

    Department updateDepartmentByIdAndDepartmentId(UUID companyId, UUID departmentId, Department department);

    Department getDepartmentByIdAndCompanyId(UUID departmentId, UUID companyId);

    Department getDepartmentById(UUID departmentId);
}

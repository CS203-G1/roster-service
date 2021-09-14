package csd.roster.service;

import csd.roster.model.Department;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    Department addDepartment(Department department);

    List<Department> getAllDepartments();

    Department getDepartmentById(UUID id);

    Department updateCompanyById(UUID id);

    Department deleteDepartmentById(UUID id);
}

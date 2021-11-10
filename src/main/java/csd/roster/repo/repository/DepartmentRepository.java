package csd.roster.repo.repository;

import csd.roster.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    Optional<Department> findByIdAndCompanyId(UUID departmentId, UUID companyId);
}

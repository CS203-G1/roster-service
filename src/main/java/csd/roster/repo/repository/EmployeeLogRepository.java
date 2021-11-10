package csd.roster.repo.repository;

import csd.roster.model.EmployeeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmployeeLogRepository extends JpaRepository<EmployeeLog, UUID> {
}

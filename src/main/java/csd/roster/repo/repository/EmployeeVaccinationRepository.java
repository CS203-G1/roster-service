package csd.roster.repo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import csd.roster.domain.model.EmployeeVaccination;

@Repository
public interface EmployeeVaccinationRepository extends JpaRepository<EmployeeVaccination, UUID> {
    List<EmployeeVaccination> findByEmployeeId(UUID employeeId);
    Optional<EmployeeVaccination> findByIdAndEmployeeId(UUID employeeVaccinationId, UUID employeeId);
}
package csd.roster.repository;

import csd.roster.model.WorkLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkLocationRepository extends JpaRepository<WorkLocation, UUID> {
    Optional<WorkLocation> findByIdAndDepartmentId(UUID workLocationId, UUID departmentId);
}

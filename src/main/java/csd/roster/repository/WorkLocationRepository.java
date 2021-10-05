package csd.roster.repository;

import csd.roster.model.WorkLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkLocationRepository extends JpaRepository<WorkLocation, UUID> {
    Optional<WorkLocation> findByIdAndDepartmentId(UUID workLocationId, UUID departmentId);

    @Query("select wl from WorkLocation wl where wl.name = 'remote' and wl.department.company.id = :id")
    Optional<WorkLocation> findRemoteWorkLocationByCompanyId(@Param("id") UUID companyId);

    @Query("select wl from WorkLocation wl where wl.department.company.id = :id")
    List<WorkLocation> findAllByCompanyId(@Param("id") UUID companyId);
}

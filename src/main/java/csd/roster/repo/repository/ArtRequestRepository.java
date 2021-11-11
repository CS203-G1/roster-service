package csd.roster.repo.repository;

import csd.roster.domain.enumerator.RequestStatus;
import csd.roster.domain.model.ArtRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArtRequestRepository extends JpaRepository<ArtRequest, UUID> {

    @Query("select ar from ArtRequest ar where ar.employee.id = :employeeId and ar.requestStatus = :requestStatus")
    List<ArtRequest> findAllByEmployeeIdAndRequestStatus(@Param("employeeId") UUID employeeId, @Param("requestStatus") RequestStatus requestStatus);
}

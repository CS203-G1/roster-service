package csd.roster.repository;

import csd.roster.model.ArtRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArtRequestRepository extends JpaRepository<ArtRequest, UUID> {
}

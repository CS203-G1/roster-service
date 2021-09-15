package csd.roster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import csd.roster.model.Roster;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RosterRepository extends JpaRepository<Roster, UUID> {
    Optional<Roster> findByIdAndCompanyId(UUID rosterId, UUID companyId);
}

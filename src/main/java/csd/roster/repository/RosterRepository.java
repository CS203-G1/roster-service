package csd.roster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import csd.roster.model.Roster;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RosterRepository extends JpaRepository<Roster, UUID> {
    Optional<Roster> findByIdAndWorkLocationId(UUID rosterId, UUID workLocationId);

    List<Roster> findByWorkLocationId(UUID workLocationId);

    Optional<Roster> findByWorkLocationIdAndDate(UUID workLocationId, LocalDate now);

    Optional<Roster> findByEmployeeIdAndDate(UUID employee, LocalDate date);
}
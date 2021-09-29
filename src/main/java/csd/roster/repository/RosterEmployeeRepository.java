package csd.roster.repository;

import csd.roster.model.RosterEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RosterEmployeeRepository extends JpaRepository<RosterEmployee, UUID> {
}
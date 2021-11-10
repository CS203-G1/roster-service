package csd.roster.repo.repository;

import csd.roster.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

//    Optional<Roster> findByWorkLocationIdAndDate(UUID workLocationId, LocalDate now);


    @Query("select r from Roster r where r.workLocation.id = :id and r.date = :date")
    Optional<Roster> findByWorkLocationIdAndDate(@Param("id") UUID workLocationId, @Param("date") LocalDate now); //(@Param("id") UUID employee, @Param("date") LocalDate date);

    @Query("select r from Roster r where r in (select re.roster from RosterEmployee re where re.employee.id = :id) and r.date = :date")
    List<Roster> findByEmployeeIdAndDate(@Param("id") UUID employee, @Param("date") LocalDate date);

    @Query("select e from Employee e where e in (select re.employee from RosterEmployee re where re.roster.id = :id)")
    List<Employee> findEmployeesByRosterId(@Param("id") UUID rosterId);

    @Query("select e from Employee e where e in (select re.employee from RosterEmployee re where re.roster.id = :id and re.isRemote = false)")
    List<Employee> findOnsiteEmployeesByRosterId(@Param("id") UUID rosterId);
}
package csd.roster.repo.repository;

import csd.roster.model.RosterEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RosterEmployeeRepository extends JpaRepository<RosterEmployee, UUID> {
    Optional<RosterEmployee> findByRosterIdAndEmployeeId(UUID rosterId, UUID employeeId);

    @Query("select re from RosterEmployee re where re.roster.id = :id")
    List<RosterEmployee> findAllByRosterId(@Param("id") UUID rosterId);

    @Query("select re from RosterEmployee re where re.roster.id = :id and re.isRemote = false")
    List<RosterEmployee> findAllOnsiteByRosterId(@Param("id") UUID rosterId);

    @Query("select re from RosterEmployee re where re.employee.company.id = :id and re.roster.date = :date and re.isRemote = true")
    List<RosterEmployee> findRemoteRosterEmployeesByCompanyIdAndDate(@Param("id") UUID companyId, @Param("date") LocalDate date);

    @Query("select re from RosterEmployee re where re.employee.company.id = :id and re.roster.date = :date")
    List<RosterEmployee> findAllRosterEmployeesByCompanyIdAndDate(@Param("id") UUID companyId, @Param("date") LocalDate date);

    @Query("select re from RosterEmployee re where re.employee.company.id = :id and re.roster.date = :date and re.isRemote = false")
    List<RosterEmployee> findOnsiteRosterEmployeesByCompanyIdAndDate(@Param("id") UUID companyId, @Param("date") LocalDate date);
}

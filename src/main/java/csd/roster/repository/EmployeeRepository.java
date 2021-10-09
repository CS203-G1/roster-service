package csd.roster.repository;

import csd.roster.enumerator.HealthStatus;
import csd.roster.model.Employee;
import csd.roster.model.WorkLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByIdAndDepartmentId(UUID employeeId, UUID departmentId);

    @Query("select e from Employee e where e.department.company.id = :id")
    List<Employee> findAllByCompanyId(@Param("id") UUID companyId);

    @Query("select e from Employee e where e.department.company.id = :id and e.createdAt <= :datetime")
    List<Employee> findAllByCompanyIdBeforeDate(@Param("id") UUID companyId, @Param("datetime") LocalDateTime datetime);

    // Assuming that only healthy employees are allowed to be at work
    // TODO: can be more efficient
    @Query("select e from Employee e where e.department.company.id = :id and " +
            "e in (select el.employee from EmployeeLog el where el.date = :date and " +
            "el.healthStatus <> csd.roster.enumerator.HealthStatus.HEALTHY)")
    List<Employee> findAllOnLeaveByCompanyIdAndDate(@Param("id") UUID companyId, @Param("date") LocalDate date);

    @Query("select e from Employee e where e.department.company.id = :id and " +
            "e in (select el.employee from EmployeeLog el where el.date = :date and " +
            "el.healthStatus = :healthStatus)")
    List<Employee> findAllByCompanyIdAndDateAndHealthStatus(@Param("id") UUID companyId,
                                                            @Param("date") LocalDate date,
                                                            @Param("healthStatus") HealthStatus healthStatus);
}

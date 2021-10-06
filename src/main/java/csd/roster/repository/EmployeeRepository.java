package csd.roster.repository;

import csd.roster.model.Employee;
import csd.roster.model.WorkLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByIdAndDepartmentId(UUID employeeId, UUID departmentId);

    @Query("select e from Employee e where e.department.company.id = :id")
    List<Employee> findAllByCompanyId(@Param("id") UUID companyId);

    @Query("select e from Employee e where e.createdAt <= :date")
    List<Employee> findAllByCompanyIdBeforeDate(@Param("date") LocalDate date);
}

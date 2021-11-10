
package csd.roster.model;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import csd.roster.domain.enumerator.HealthStatus;
import csd.roster.domain.enumerator.VaccinationStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EmployeeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="employee_id", referencedColumnName = "id")
    private Employee employee;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate date;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "vaccination_status")
    private VaccinationStatus vaccinationStatus;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "health_status")
    private HealthStatus healthStatus;

    @Column(name = "is_in_company")
    private Boolean isInCompany = true;

    public EmployeeLog(Employee employee) {
        this.employee = employee;
        this.date = LocalDate.now();
        this.vaccinationStatus = employee.getVaccinationStatus();
        this.healthStatus = employee.getHealthStatus();
        this.isInCompany = employee.getIsInCompany();
    }
}


package csd.roster.model;

import java.util.Set;
import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import csd.roster.enumerator.HealthStatus;
import csd.roster.enumerator.VaccinationStatus;
import csd.roster.enumerator.VaccineBrand;
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
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @OneToMany(mappedBy = "employee")
    private Set<RosterEmployee> roster_employees;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "vaccination_status")
    private VaccinationStatus vaccinationStatus;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "vaccine_brand")
    private VaccineBrand vaccineBrand;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "health_status")
    private HealthStatus healthStatus;
}

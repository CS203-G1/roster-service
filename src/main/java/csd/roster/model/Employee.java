
package csd.roster.model;

import java.util.Set;
import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import csd.roster.enumerator.HealthStatus;
import csd.roster.enumerator.VaccinationStatus;
import csd.roster.enumerator.VaccinationBrand;
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
    @JsonIgnore
    private Set<RosterEmployee> roster_employees;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "vaccination_status")
    private VaccinationStatus vaccinationStatus;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "vaccination_brand")
    private VaccinationBrand vaccinationBrand;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "health_status")
    private HealthStatus healthStatus;
}

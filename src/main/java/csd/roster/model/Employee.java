
package csd.roster.model;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.CreationTimestamp;

import csd.roster.enumerator.HealthStatus;
import csd.roster.enumerator.VaccinationBrand;
import csd.roster.enumerator.VaccinationStatus;
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

    @ManyToOne
    @JsonIgnore
    @JoinColumns({
            @JoinColumn(name="company_id", referencedColumnName="company_id"),
            @JoinColumn(name="department_id", referencedColumnName="id")
    })
    private Department department;

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

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "is_in_company")
    private Boolean isInCompany = true;
}

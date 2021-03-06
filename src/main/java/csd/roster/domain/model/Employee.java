package csd.roster.domain.model;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.CreationTimestamp;

import csd.roster.domain.enumerator.HealthStatus;
import csd.roster.domain.enumerator.VaccinationBrand;
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
public class Employee {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @JsonIgnore
    @Transient
    @OneToMany(mappedBy = "employee", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<RosterEmployee> roster_employees;

    @JsonIgnore
    @Transient
    @OneToMany(mappedBy = "employee", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Request> requests;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="department_id", referencedColumnName="id")
    private Department department;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="work_location_id", referencedColumnName="id")
    private WorkLocation workLocation;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="company_id", referencedColumnName="id")
    private Company company;

    @JsonIgnore
    @Transient
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<EmployeeLog> employeeLogs;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "position")
    private String position;

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
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_in_company")
    private Boolean isInCompany = true;
}

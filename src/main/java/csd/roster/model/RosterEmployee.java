package csd.roster.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;

import csd.roster.annotation.ValidDateTimes;
import csd.roster.enumerator.HealthStatus;
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
// To validate that fromDateTime is before toDateTime
@ValidDateTimes(fromDateTime = "fromDateTime", toDateTime = "toDateTime")
public class RosterEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    // Note that in lecture notes prof used many-to-many since there is no need to use relationship entity
    // Here I am using Many-To-One and One-To-Many since RosterEmployee is a relationship entity
    // Reference: https://www.baeldung.com/jpa-many-to-many

    @ManyToOne
    @JoinColumn(name = "roster_id")
    @NotBlank(message = "Roster must not be blank")
    Roster roster;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @NotBlank(message = "Employee must not be blank")
    Employee employee;

    // When the roster for this employee starts
    @Column(name = "from_datetime")
    @NotBlank(message = "FromDateTime must not be blank")
    @Future
    private LocalDateTime fromDateTime;

    // When the roster for this employee ends
    @Column(name = "to_datetime")
    @NotBlank(message = "ToDateTime must not be blank")
    @Future
    private LocalDateTime toDateTime;

    // The health status of the employee during the duration of this roster
    @Column(name = "current_health_status")
    private HealthStatus currentHealthStatus;
}

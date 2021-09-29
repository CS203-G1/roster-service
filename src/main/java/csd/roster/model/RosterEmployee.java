package csd.roster.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;

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
    Roster roster;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    Employee employee;

    // When the roster for this employee starts
    @Column(name = "from_datetime")
    private LocalDateTime from_datetime;

    // When the roster for this employee ends
    @Column(name = "to_datetime")
    private LocalDateTime to_datetime;

    // The health status of the employee during the duration of this roster
    @Column(name = "current_health_status")
    private HealthStatus current_health_status;
}

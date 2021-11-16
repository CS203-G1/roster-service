package csd.roster.domain.model;

import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import csd.roster.domain.enumerator.HealthStatus;
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
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueRosterAndEmployee",
        columnNames = {"roster_id", "employee_id"})})
public class RosterEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    // Note that in lecture notes prof used many-to-many since there is no need to use relationship entity
    // Here I am using Many-To-One and One-To-Many since RosterEmployee is a relationship entity
    // Reference: https://www.baeldung.com/jpa-many-to-many

    @ManyToOne
    @JoinColumn(name = "roster_id", referencedColumnName = "id")
    // @JsonBackReference means that this will be omitted in the serialization
    // Done to prevent infinite recursion
    // https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion
    @JsonBackReference
    Roster roster;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    Employee employee;

    @Column(name = "is_remote")
    private boolean isRemote;

    // The health status of the employee during the duration of this roster
    @Column(name = "current_health_status")
    private HealthStatus currentHealthStatus;
}

package csd.roster.model;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import csd.roster.annotation.ValidDateTimes;
import csd.roster.enumerator.HealthStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
// To validate that fromDateTime is before toDateTime
@ValidDateTimes(fromDateTime = "fromDateTime", toDateTime = "toDateTime")
@Table(uniqueConstraints = { @UniqueConstraint(name = "UniqueRosterAndEmployee",
        columnNames = { "roster_id", "employee_id" }) })
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
    // @JsonBackReference means that this will be omitted in the serialization
    // Done to prevent infinite recursion
    // https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion
    @JsonBackReference
    Roster roster;

    @ManyToOne
    @JoinColumn(name="employee_id", referencedColumnName="id")
    Employee employee;


    // When the roster for this employee starts
    @Column(name = "from_date_time")
    @NotNull(message = "FromDateTime must not be blank")
    @Future
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fromDateTime;

    // When the roster for this employee ends
    @Column(name = "to_date_time")
    @NotNull(message = "ToDateTime must not be blank")
    @Future
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime toDateTime;

    // The health status of the employee during the duration of this roster
    @Column(name = "current_health_status")
    private HealthStatus currentHealthStatus;
}

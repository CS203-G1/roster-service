package csd.roster.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import csd.roster.annotation.ValidDateTimes;
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
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueRosterAndEmployee",
        columnNames = {"roster_id", "employee_id"})})
public class Roster {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "date", columnDefinition = "DATE")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "workLocation_id")
    @JsonIgnore
    private WorkLocation workLocation;

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

    // @JsonManaged Reference means that this is the forward part of reference and will be serialized normally
    // Done to prevent infinite recursion
    // https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion
    @JsonIgnore
    @Transient
    @OneToMany(mappedBy = "roster", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<RosterEmployee> rosterEmployees;
}
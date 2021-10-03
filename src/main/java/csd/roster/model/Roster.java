package csd.roster.model;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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

    @OneToMany(mappedBy = "roster")
    // @JsonManaged Reference means that this is the forward part of reference and will be serialized normally
    // Done to prevent infinite recursion
    // https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion
    @JsonManagedReference
    private Set<RosterEmployee> roster_employees;
}
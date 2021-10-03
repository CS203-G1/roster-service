package csd.roster.model;

import java.util.List;
import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class WorkLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "company_id", referencedColumnName = "company_id"),
            @JoinColumn(name = "department_id", referencedColumnName = "id")
    })
    private Department department;

    @JsonIgnore
    @OneToMany(mappedBy = "workLocation", cascade = CascadeType.ALL)
    private List<Roster> rosters;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "capacity")
    private int capacity;
}

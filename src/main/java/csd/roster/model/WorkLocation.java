package csd.roster.model;

import lombok.*;
import javax.persistence.*;
import java.util.UUID;

import java.util.List;

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
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "capacity")
    private int capacity;

    @OneToMany(mappedBy = "roster", cascade = CascadeType.ALL)
    private List<Roster> rosters;
}

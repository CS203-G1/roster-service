package csd.roster.model;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WorkLocation {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="department_id")
    private Department department;

    @Column(name="name")
    private String name;

    @Column(name="address")
    private String address;

    @Column(name="capacity")
    private int capacity;
}

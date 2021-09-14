package csd.roster.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Department {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="company_id")
    private Company company;

    @Column(name="name")
    private String name;
}

package csd.roster.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Department implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="company_id")
    private Company company;

    @JsonIgnore
    @OneToMany(mappedBy = "department", orphanRemoval = true, cascade = CascadeType.ALL)
    @Transient
    private List<Employee> employees;

    @Column(name="name")
    private String name;
}

package csd.roster.model;

import lombok.*;
import javax.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

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
    private LocalDate localDate;

    @ManyToOne
    @JoinColumn(name = "workLocation_id")
    private WorkLocation workLocation;
}
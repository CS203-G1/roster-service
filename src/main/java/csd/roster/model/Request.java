package csd.roster.model;

import csd.roster.domain.enumerator.RequestStatus;
import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="employee_id", referencedColumnName = "id")
    private Employee employee;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    RequestStatus requestStatus = RequestStatus.PENDING;
}

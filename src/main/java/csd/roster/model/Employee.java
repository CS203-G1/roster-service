
package csd.roster.model;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import csd.roster.enumerator.HealthStatus;
import csd.roster.enumerator.VaccinationStatus;
import csd.roster.enumerator.VaccineBrand;
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
public class Employee {
    
}

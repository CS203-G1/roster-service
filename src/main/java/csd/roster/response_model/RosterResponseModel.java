package csd.roster.response_model;

import csd.roster.domain.model.Employee;
import csd.roster.domain.model.Roster;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RosterResponseModel {
    private Roster roster;

    private List<Employee> employees;
}


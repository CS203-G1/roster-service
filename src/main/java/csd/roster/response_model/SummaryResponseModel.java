package csd.roster.response_model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SummaryResponseModel {
    // Total number of employees for a certain company
    private int employeesCount;
    private float employeesCountChange;

    // Total number of employees on leave for a certain company
    private int leaveCount;
    private float leaveCountChange;

    // Total number of employees working on site
    private int onsiteCount;
    private float onsiteCountChange;

    // Total number of employees contracted with covid 
    private int covidCount;
    private float covidCountChange;
}

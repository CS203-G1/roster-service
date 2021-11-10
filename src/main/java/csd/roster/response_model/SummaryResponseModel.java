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
    private int employeesCountChange;

    // Total number of employees on leave for a certain company
    private int leaveCount;
    private int leaveCountChange;

    // Total number of employees working on site
    private int onsiteCount;
    private int onsiteCountChange;

    // Total number of employees contracted with covid 
    private int covidCount;
    private int covidCountChange;
}

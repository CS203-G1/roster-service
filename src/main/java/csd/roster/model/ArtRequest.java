package csd.roster.model;

import csd.roster.enumerator.HealthStatus;
import csd.roster.exception.exceptions.EmployeeNotFoundException;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.net.URL;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ArtRequest extends Request{

    @Enumerated(EnumType.STRING)
    @Column(name = "health_status")
    private HealthStatus healthStatus;

    @Column(name = "image_url")
    private URL imageUrl;

    public String getFilePath(){
        if(this.getEmployee() == null ){
            throw new EmployeeNotFoundException(null);
        }
        Employee employee = this.getEmployee();
        StringBuilder builder = new StringBuilder();
        builder.append("art_requests/");
        builder.append("Company_").append(employee.getCompany().getId()).append("/");
        builder.append("Department_").append(employee.getDepartment().getId()).append("/");
        builder.append("Employee_").append(employee.getId()).append("/");
        builder.append(LocalDate.now());
        return builder.toString();

    }


}

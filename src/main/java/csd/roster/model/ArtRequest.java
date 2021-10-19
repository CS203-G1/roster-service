package csd.roster.model;

import csd.roster.enumerator.HealthStatus;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;


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

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "image_file_name")
    private String imageFileName;
}

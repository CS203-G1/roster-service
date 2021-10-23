package csd.roster.holder;

import csd.roster.enumerator.HealthStatus;
import csd.roster.enumerator.RequestStatus;
import lombok.*;
import org.springframework.boot.actuate.health.Health;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ArtRequestReviewHolder {
    private HealthStatus healthStatus;
    private RequestStatus requestStatus;
}

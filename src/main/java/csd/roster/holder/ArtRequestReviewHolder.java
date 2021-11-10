package csd.roster.holder;

import csd.roster.domain.enumerator.HealthStatus;
import csd.roster.domain.enumerator.RequestStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ArtRequestReviewHolder {
    private HealthStatus healthStatus;
    private RequestStatus requestStatus;
}

package csd.roster.domain.response_model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WorkingStatisticResponseModel {
    private UUID companyId;

    private int onsiteCount;

    private int remoteCount;
}

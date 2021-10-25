package csd.roster.service.interfaces;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface SchedulerService {
    Map<Integer, Set<UUID>> scheduleRoster(UUID workLocationId);
}

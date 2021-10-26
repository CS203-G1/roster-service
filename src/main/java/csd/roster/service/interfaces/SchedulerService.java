package csd.roster.service.interfaces;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SchedulerService {
    Map<Integer, List<UUID>> scheduleRoster(UUID workLocationId);
}

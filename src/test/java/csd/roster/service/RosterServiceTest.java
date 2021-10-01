package csd.roster.service;

import csd.roster.model.Roster;
import csd.roster.model.WorkLocation;
import csd.roster.repository.RosterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RosterServiceTest {
    @Mock
    RosterRepository rosters;

    @Mock
    WorkLocationServiceImpl workLocationService;

    @InjectMocks
    RosterServiceImpl rosterService;

    @Test
    public void addRoster_NewRoster_ReturnSavedRoster(){
        UUID workLocationId = UUID.randomUUID();
        WorkLocation workLocation = new WorkLocation(workLocationId, null, null, null, 40, null);

        UUID rosterId = UUID.randomUUID();
        Roster roster = new Roster(rosterId, LocalDate.now(), null, null);

        when(workLocationService.getWorkLocationById(workLocationId)).thenReturn(workLocation);
        when(rosters.save(any(Roster.class))).thenReturn(roster);

        Roster savedRoster = rosterService.addRoster(workLocationId, roster);

        assertNotNull(savedRoster);
        assertEquals(workLocation, savedRoster.getWorkLocation());

        verify(workLocationService,times(1)).getWorkLocationById(workLocationId);
        verify(rosters, times(1)).save(roster);
    }
}

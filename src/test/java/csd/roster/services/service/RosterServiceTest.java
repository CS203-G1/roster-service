package csd.roster.services.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.roster.domain.exception.exceptions.RosterNotFoundException;
import csd.roster.domain.model.Roster;
import csd.roster.domain.model.WorkLocation;
import csd.roster.repo.repository.RosterRepository;

@ExtendWith(MockitoExtension.class)
public class RosterServiceTest {
    @Mock
    RosterRepository rosters;

    @Mock
    WorkLocationServiceImpl workLocationService;

    @InjectMocks
    RosterServiceImpl rosterService;

    @Test
    public void addRoster_NewRoster_ReturnSavedRoster() {
        UUID workLocationId = UUID.randomUUID();
        WorkLocation workLocation = new WorkLocation(workLocationId, null, null, null, null, null, 40);

        UUID rosterId = UUID.randomUUID();
        Roster roster = new Roster(rosterId, LocalDate.now(), null, null, null, null);

        when(workLocationService.getWorkLocationById(workLocationId)).thenReturn(workLocation);
        when(rosters.save(any(Roster.class))).thenReturn(roster);

        Roster savedRoster = rosterService.addRoster(workLocationId, roster);

        assertNotNull(savedRoster);
        assertEquals(workLocation, savedRoster.getWorkLocation());

        verify(workLocationService, times(1)).getWorkLocationById(workLocationId);
        verify(rosters, times(1)).save(roster);
    }

    @Test
    public void getRosters_RostersExist_ReturnListOfRosters() {
        List<Roster> allRostersInWorkLocation = new ArrayList<Roster>();

        UUID workLocationId = UUID.randomUUID();
        WorkLocation workLocation = new WorkLocation(workLocationId, null, allRostersInWorkLocation, null, null, null, 40);

        UUID rosterId = UUID.randomUUID();
        Roster roster = new Roster(rosterId, LocalDate.now(), workLocation, null, null, null);

        allRostersInWorkLocation.add(roster);

        when(rosters.findByWorkLocationId(workLocationId)).thenReturn(workLocation.getRosters());

        List<Roster> rostersForWorkLocation = rosterService.getRostersByWorkLocationId(workLocationId);

        assertNotNull(rostersForWorkLocation);
        assertEquals(1, rostersForWorkLocation.size());

        verify(rosters, times(1)).findByWorkLocationId(workLocationId);
    }

    @Test
    public void getRosters_EmptyRosters_ThrowException() {
        List<Roster> allRostersInWorkLocation = new ArrayList<Roster>();

        UUID workLocationId = UUID.randomUUID();
        WorkLocation workLocation = new WorkLocation(workLocationId, null, allRostersInWorkLocation, null, null, null, 40);

        when(rosters.findByWorkLocationId(workLocationId)).thenReturn(workLocation.getRosters());

        Exception exception = assertThrows(RosterNotFoundException.class, () -> rosterService.getRostersByWorkLocationId(workLocationId));
        String expectedExceptionMessage = String.format("Work location %s does not contain any rosters", workLocationId);

        assertEquals(expectedExceptionMessage, exception.getMessage());
        verify(rosters, times(1)).findByWorkLocationId(workLocationId);
    }

    @Test
    public void getRosterWithIdAndWorkLocationId_RosterExists_ReturnFoundRoster() {
        List<Roster> allRostersInWorkLocation = new ArrayList<Roster>();

        UUID workLocationId = UUID.randomUUID();
        WorkLocation workLocation = new WorkLocation(workLocationId, null, allRostersInWorkLocation, null,null,  null, 40);

        UUID rosterId = UUID.randomUUID();
        Roster roster = new Roster(rosterId, LocalDate.now(), workLocation, null, null, null);

        when(rosters.findByIdAndWorkLocationId(rosterId,workLocationId)).thenReturn(Optional.of(roster));

        Roster foundRoster = rosterService.getRosterByIdAndWorkLocationId(workLocationId, rosterId);

        assertEquals(roster, foundRoster);

        verify(rosters, times(1)).findByIdAndWorkLocationId(rosterId, workLocationId);
    }

    @Test
    public void getRosterWithIdAndWorkLocationId_RosterDoesNotExist_ThrowException() {
        List<Roster> allRostersInWorkLocation = new ArrayList<Roster>();

        UUID workLocationId = UUID.randomUUID();
        WorkLocation workLocation = new WorkLocation(workLocationId, null, allRostersInWorkLocation, null, null, null, 40);

        UUID rosterId = UUID.randomUUID();
        Roster roster = new Roster(rosterId, LocalDate.now(), workLocation, null, null, null);


        Exception exception = assertThrows(RosterNotFoundException.class, () -> rosterService.getRosterByIdAndWorkLocationId(workLocationId, rosterId));
        String expectedExceptionMessage = String.format("Unable to find roster %s from work location %s",
                rosterId, workLocationId);

        assertEquals(expectedExceptionMessage, exception.getMessage());
        verify(rosters, times(1)).findByIdAndWorkLocationId(rosterId, workLocationId);
    }

    @Test
    public void getRosterWithId_RosterExists_ReturnFoundRoster() {
        List<Roster> allRostersInWorkLocation = new ArrayList<Roster>();

        UUID workLocationId = UUID.randomUUID();
        WorkLocation workLocation = new WorkLocation(workLocationId, null, allRostersInWorkLocation,null,  null, null, 40);

        UUID rosterId = UUID.randomUUID();
        Roster roster = new Roster(rosterId, LocalDate.now(), workLocation, null, null, null);

        when(rosters.findById(rosterId)).thenReturn(Optional.of(roster));

        Roster foundRoster = rosterService.getRosterById(rosterId);

        assertEquals(roster, foundRoster);

        verify(rosters, times(1)).findById(rosterId);
    }

    @Test
    public void getRosterWithId_RosterDoesNotExist_ThrowException() {
        List<Roster> allRostersInWorkLocation = new ArrayList<Roster>();

        UUID workLocationId = UUID.randomUUID();
        WorkLocation workLocation = new WorkLocation(workLocationId, null,  allRostersInWorkLocation, null, null, null, 40);

        UUID rosterId = UUID.randomUUID();
        Roster roster = new Roster(rosterId, LocalDate.now(), workLocation, null, null, null);

        Exception exception = assertThrows(RosterNotFoundException.class, () -> rosterService.getRosterById(rosterId));
        String expectedExceptionMessage = String.format("Roster %s does not exist", rosterId);

        assertEquals(expectedExceptionMessage, exception.getMessage());
        verify(rosters, times(1)).findById(rosterId);
    }
}

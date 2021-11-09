package csd.roster.service;

import csd.roster.enumerator.HealthStatus;
import csd.roster.exception.exceptions.EmployeeNotHealthyException;
import csd.roster.exception.exceptions.RosterEmployeeNotFoundException;
import csd.roster.model.Employee;
import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;
import csd.roster.repository.RosterEmployeeRepository;
import csd.roster.service.interfaces.EmployeeService;
import csd.roster.service.interfaces.RosterEmployeeService;
import csd.roster.service.interfaces.RosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class RosterEmployeeServiceImpl implements RosterEmployeeService {
    private final RosterEmployeeRepository rosterEmployeeRepository;
    private final RosterService rosterService;
    private final EmployeeService employeeService;

    @Autowired
    public RosterEmployeeServiceImpl(RosterEmployeeRepository rosterEmployeeRepository,
                                     RosterService rosterService,
                                     EmployeeService employeeService) {
        this.rosterEmployeeRepository = rosterEmployeeRepository;
        this.rosterService = rosterService;
        this.employeeService = employeeService;
    }

    @Override
    public RosterEmployee addRosterEmployee(final UUID rosterId,
                                            final UUID employeeId,
                                            final RosterEmployee rosterEmployee) {
        Roster roster = rosterService.getRoster(rosterId);
        Employee employee = employeeService.getEmployee(employeeId);

        rosterEmployee.setEmployee(employee);

        if (employee.getHealthStatus() != HealthStatus.HEALTHY) {
            throw new EmployeeNotHealthyException(employee);
        }

        rosterEmployee.setCurrentHealthStatus(employee.getHealthStatus());
        rosterEmployee.setRoster(roster);

        return rosterEmployeeRepository.save(rosterEmployee);
    }

    @Override
    public RosterEmployee getRosterEmployee(final UUID rosterId, final UUID employeeId) {
        return rosterEmployeeRepository.findByRosterIdAndEmployeeId(rosterId, employeeId)
                .orElseThrow(() -> new RosterEmployeeNotFoundException(rosterId, employeeId));
    }

    @Override
    public void removeRosterEmployee(final UUID rosterId, final UUID employeeId) {
        RosterEmployee rosterEmployee = getRosterEmployee(rosterId, employeeId);

        rosterEmployeeRepository.delete(rosterEmployee);
    }

    @Override
    public RosterEmployee updateRosterEmployee(final UUID rosterId,
                                               final UUID employeeId,
                                               final RosterEmployee newRosterEmployee) {
        RosterEmployee rosterEmployee = getRosterEmployee(rosterId, employeeId);

        // Different from the usual updates because we only want to allow the frontend request to be able to
        // update from date time and to date time
        rosterEmployee.setRemote(newRosterEmployee.isRemote());

        return rosterEmployeeRepository.save(rosterEmployee);
    }

    @Override
    public List<RosterEmployee> findAllRosterEmployeesByCompanyIdAndDate(final UUID companyId,
                                                                         final LocalDate date) {
        return rosterEmployeeRepository.findAllRosterEmployeesByCompanyIdAndDate(companyId, date);
    }

    @Override
    public List<RosterEmployee> findRemoteRosterEmployeesByCompanyIdAndDate(final UUID companyId,
                                                                            final LocalDate date) {
        return rosterEmployeeRepository.findRemoteRosterEmployeesByCompanyIdAndDate(companyId, date);
    }

    @Override
    public List<RosterEmployee> findOnsiteRosterEmployeesByCompanyIdAndDate(final UUID companyId,
                                                                            final LocalDate date) {
        return rosterEmployeeRepository.findOnsiteRosterEmployeesByCompanyIdAndDate(companyId, date);
    }
}

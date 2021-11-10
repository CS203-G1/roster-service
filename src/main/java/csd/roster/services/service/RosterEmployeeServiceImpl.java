package csd.roster.services.service;

import csd.roster.domain.enumerator.HealthStatus;
import csd.roster.domain.exception.exceptions.EmployeeNotHealthyException;
import csd.roster.domain.exception.exceptions.RosterEmployeeNotFoundException;
import csd.roster.domain.exception.exceptions.RuleViolatedException;
import csd.roster.model.Employee;
import csd.roster.model.Roster;
import csd.roster.model.RosterEmployee;
import csd.roster.repo.repository.RosterEmployeeRepository;
import csd.roster.services.service.interfaces.EmployeeService;
import csd.roster.services.service.interfaces.RosterEmployeeService;
import csd.roster.services.service.interfaces.RosterService;
import csd.roster.services.service.interfaces.WorkLocationService;
import org.hibernate.jdbc.Work;
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

        List<Employee> allRosterEmployees = employeeService.getAllEmployeesByWorkLocationId(roster.getWorkLocation().getId());
        List<RosterEmployee> onsiteRosterEmployees = rosterEmployeeRepository.findAllOnsiteByRosterId(rosterId);

        int maximumOnsiteCount = (int) (allRosterEmployees.size() * 0.50);

        if (onsiteRosterEmployees.size() >= maximumOnsiteCount) {
            throw new RuleViolatedException(
                    String.format("You can only assign %d employees to this roster", maximumOnsiteCount));
        }

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
        return rosterEmployeeRepository.findByRosterIdAndEmployeeId(rosterId, employeeId).get(0);
    }

    @Override
    public void removeRosterEmployee(final UUID rosterId, final UUID employeeId) {
        RosterEmployee rosterEmployee = getRosterEmployee(rosterId, employeeId);

        rosterEmployee.setRemote(true);

        rosterEmployeeRepository.save(rosterEmployee);
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

package csd.roster.services.service;

import java.util.List;
import java.util.UUID;

import csd.roster.services.service.interfaces.EmployeeService;
import csd.roster.services.service.interfaces.EmployeeVaccinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import csd.roster.domain.exception.exceptions.EmployeeVaccinationNotFoundException;
import csd.roster.model.Employee;
import csd.roster.model.EmployeeVaccination;
import csd.roster.repo.repository.EmployeeVaccinationRepository;

@Service
public class EmployeeVaccinationServiceImpl implements EmployeeVaccinationService {
    private final EmployeeVaccinationRepository employeeVaccinationRepository;
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeVaccinationServiceImpl(EmployeeVaccinationRepository employeeVaccinationRepository, EmployeeService employeeService) {
        this.employeeVaccinationRepository = employeeVaccinationRepository;
        this.employeeService = employeeService;
    }

    @Override
    public EmployeeVaccination addEmployeeVaccination(final UUID employeeId,
                                                      final EmployeeVaccination employeeVaccination) {
        Employee employee = employeeService.getEmployee(employeeId);
        employeeVaccination.setEmployee(employee);

        return employeeVaccinationRepository.save(employeeVaccination);
    }

    @Override
    public List<EmployeeVaccination> getEmployeeVaccinations(final UUID employeeId) {
        List<EmployeeVaccination> employeeVaccination = employeeVaccinationRepository.findByEmployeeId(employeeId);

        if (employeeVaccination.isEmpty()) {
            throw new EmployeeVaccinationNotFoundException(employeeId);
        }
        return employeeVaccination;
    }

    @Override
    public EmployeeVaccination getEmployeeVaccination(final UUID employeeId,
                                                      final UUID employeeVaccinationId) {
        return employeeVaccinationRepository.findByIdAndEmployeeId(employeeVaccinationId, employeeId)
                .orElseThrow(() -> new EmployeeVaccinationNotFoundException(employeeVaccinationId, employeeId));
    }

    @Override
    public EmployeeVaccination updateEmployeeVaccination(final UUID employeeId,
                                                         final UUID employeeVaccinationId,
                                                         final EmployeeVaccination employeeVaccination) {
        return employeeVaccinationRepository.findByIdAndEmployeeId(employeeVaccinationId, employeeId).map(oldEmployeeVaccination -> {
            oldEmployeeVaccination.setVaccinationBrand(employeeVaccination.getVaccinationBrand());
            oldEmployeeVaccination.setVaccinationCount(employeeVaccination.getVaccinationCount());
            oldEmployeeVaccination.setCreatedAt(employeeVaccination.getCreatedAt());

            return employeeVaccinationRepository.save(oldEmployeeVaccination);

        }).orElseThrow(() -> new EmployeeVaccinationNotFoundException(employeeVaccinationId, employeeId));
    }

    @Override
    public void deleteEmployeeVaccination(final UUID employeeId,
                                          final UUID employeeVaccinationId) {
        EmployeeVaccination employeeVaccination = getEmployeeVaccination(employeeId, employeeVaccinationId);

        employeeVaccinationRepository.delete(employeeVaccination);
    }
}

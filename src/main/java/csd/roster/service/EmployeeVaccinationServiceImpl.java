package csd.roster.service;

import java.util.List;
import java.util.UUID;

import csd.roster.service.interfaces.EmployeeService;
import csd.roster.service.interfaces.EmployeeVaccinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import csd.roster.exception.exceptions.EmployeeVaccinationNotFoundException;
import csd.roster.model.Employee;
import csd.roster.model.EmployeeVaccination;
import csd.roster.repository.EmployeeVaccinationRepository;

@Service
public class EmployeeVaccinationServiceImpl implements EmployeeVaccinationService {
    private EmployeeVaccinationRepository employeeVaccinationRepository;
    private EmployeeService employeeService;

    @Autowired
    public EmployeeVaccinationServiceImpl(EmployeeVaccinationRepository employeeVaccinationRepository, EmployeeService employeeService) {
        this.employeeVaccinationRepository = employeeVaccinationRepository;
        this.employeeService = employeeService;
    }

    @Override
    public EmployeeVaccination addEmployeeVaccination(UUID employeeId, EmployeeVaccination employeeVaccination) {
        Employee employee = employeeService.getEmployee(employeeId);
        employeeVaccination.setEmployee(employee);

        return employeeVaccinationRepository.save(employeeVaccination);
    }

    @Override
    public List<EmployeeVaccination> getEmployeeVaccinations(UUID employeeId) {
        List<EmployeeVaccination> employeeVaccination = employeeVaccinationRepository.findByEmployeeId(employeeId);

        if (employeeVaccination.isEmpty()) {
            throw new EmployeeVaccinationNotFoundException(employeeId);
        }
        return employeeVaccination;
    }

    @Override
    public EmployeeVaccination getEmployeeVaccination(UUID employeeId, UUID employeeVaccinationId) {
        return employeeVaccinationRepository.findByIdAndEmployeeId(employeeVaccinationId, employeeId)
                .orElseThrow(() -> new EmployeeVaccinationNotFoundException(employeeVaccinationId, employeeId));
    }

    @Override
    public EmployeeVaccination updateEmployeeVaccination(UUID employeeId, UUID employeeVaccinationId, EmployeeVaccination employeeVaccination) {
        return employeeVaccinationRepository.findByIdAndEmployeeId(employeeVaccinationId, employeeId).map(oldEmployeeVaccination -> {
            oldEmployeeVaccination.setVaccinationBrand(employeeVaccination.getVaccinationBrand());
            oldEmployeeVaccination.setVaccinationCount(employeeVaccination.getVaccinationCount());
            oldEmployeeVaccination.setCreatedAt(employeeVaccination.getCreatedAt());

            return employeeVaccinationRepository.save(oldEmployeeVaccination);

        }).orElseThrow(() -> new EmployeeVaccinationNotFoundException(employeeVaccinationId, employeeId));
    }

    @Override
    public void deleteEmployeeVaccination(UUID employeeId, UUID employeeVaccinationId) {
        EmployeeVaccination employeeVaccination = getEmployeeVaccination(employeeId, employeeVaccinationId);

        employeeVaccinationRepository.delete(employeeVaccination);
    }
}

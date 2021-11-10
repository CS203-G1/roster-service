package csd.roster.services.service.interfaces;

import java.util.List;
import java.util.UUID;

import csd.roster.model.EmployeeVaccination;

public interface EmployeeVaccinationService {
    EmployeeVaccination addEmployeeVaccination(UUID employeeId, EmployeeVaccination employeeVaccination);
    List<EmployeeVaccination> getEmployeeVaccinations(UUID employeeId);
    EmployeeVaccination getEmployeeVaccination(UUID employeeId, UUID employeeVaccinationId);
    EmployeeVaccination updateEmployeeVaccination(UUID employeeId, UUID employeeVaccinationId, EmployeeVaccination employeeVaccination);
    void deleteEmployeeVaccination(UUID employeeId, UUID employeeVaccinationId);
}

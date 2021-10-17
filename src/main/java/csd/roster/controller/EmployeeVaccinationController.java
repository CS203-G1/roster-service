package csd.roster.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import csd.roster.model.EmployeeVaccination;
import csd.roster.service.interfaces.EmployeeVaccinationService;

@RestController
@PreAuthorize("hasRole('ROLE_EMPLOYER')")
public class EmployeeVaccinationController {
    private EmployeeVaccinationService employeeVaccinationService;

    @Autowired
    public EmployeeVaccinationController(EmployeeVaccinationService employeeVaccinationService) {
        this.employeeVaccinationService = employeeVaccinationService;
    }

    @GetMapping("/employees/{employeeId}/employee-vaccinations/{employeeVaccinationId}")
    public EmployeeVaccination getEmployeeVaccination(@PathVariable(value = "employeeId") UUID employeeId,
                            @PathVariable(value = "employeeVaccinationId") UUID employeeVaccinationId) {
        return employeeVaccinationService.getEmployeeVaccination(employeeId, employeeVaccinationId);
    }

    @GetMapping("/employees/{employeeId}/employee-vaccinations")
    public List<EmployeeVaccination> getEmployeeVaccinations(@PathVariable(value = "employeeId") UUID employeeId) {
        return employeeVaccinationService.getEmployeeVaccinations(employeeId);
    }

    @PostMapping("/employees/{employeeId}/employee-vaccinations")
    public EmployeeVaccination addEmployeeVaccination(@PathVariable(value = "employeeId") UUID employeeId,
                            @Valid @RequestBody EmployeeVaccination employeeVaccination) {
        return employeeVaccinationService.addEmployeeVaccination(employeeId, employeeVaccination);
    }

    @PutMapping("/employees/{employeeId}/employee-vaccinations/{employeeVaccinationId}")
    public EmployeeVaccination updateEmployeeVaccination(@PathVariable(value = "employeeId") UUID employeeId,
                            @PathVariable(value = "employeeVaccinationId") UUID employeeVaccinationId,
                            @Valid @RequestBody EmployeeVaccination employeeVaccination) {
        return employeeVaccinationService.updateEmployeeVaccination(employeeId, employeeVaccinationId, employeeVaccination);
    }

    @DeleteMapping("/employees/{employeeId}/employee-vaccinations/{employeeVaccinationId}")
    public void deleteEmployeeVaccination(@PathVariable(value = "employeeId") UUID employeeId,
                            @PathVariable(value = "employeeVaccinationId") UUID employeeVaccinationId) {
        employeeVaccinationService.deleteEmployeeVaccination(employeeId, employeeVaccinationId);
    }
}

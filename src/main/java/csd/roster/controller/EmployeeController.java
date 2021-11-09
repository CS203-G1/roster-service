package csd.roster.controller;

import java.util.UUID;

import javax.validation.Valid;

import csd.roster.model.Employee;
import csd.roster.service.interfaces.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ROLE_EMPLOYER')")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/departments/{departmentId}/employees")
    public Employee addEmployee(@PathVariable(value = "departmentId") UUID departmentId,
                                @Valid @RequestBody Employee employee) {
        // Employee throws a id is null error because we are not passing in a UUID anymore
        // This is just a placeholder value to prevent the error
        // Actual UUID will be from Cognito
        return employeeService.addEmployee(departmentId, employee);
    }

    @PostMapping("/work-locations/{workLocationId}/employees/{employeeId}")
    public Employee addEmployeeToWorkLocation(@PathVariable(value = "workLocationId") UUID workLocationId,
                                              @PathVariable(value = "employeeId") UUID employeeId) {
        return employeeService.addEmployeeToWorkLocation(workLocationId, employeeId);
    }

    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    @PostMapping("/departments/{departmentId}/employers")
    public Employee addEmployer(@PathVariable(value = "departmentId") UUID departmentId,
                                @Valid @RequestBody Employee employee) {
        return employeeService.addEmployer(departmentId, employee);
    }

    @PutMapping("/departments/{departmentId}/employees/{employeeId}")
    public Employee updateEmployee(@PathVariable(value = "departmentId") UUID departmentId,
                                   @PathVariable(value = "employeeId") UUID employeeId,
                                   @Valid @RequestBody Employee employee) {
        return employeeService.updateEmployee(departmentId, employeeId, employee);
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/departments/{departmentId}/employees/{employeeId}")
    public Employee getEmployee(@PathVariable(value = "departmentId") UUID departmentId,
                                @PathVariable(value = "employeeId") UUID employeeId) {
        return employeeService.getEmployee(departmentId, employeeId);
    }

    @DeleteMapping("/departments/{departmentId}/employees/{employeeId}")
    public void deleteEmployee(@PathVariable(value = "departmentId") UUID departmentId,
                               @PathVariable(value = "employeeId") UUID employeeId) {
        employeeService.deleteEmployee(departmentId, employeeId);
    }

    @GetMapping("companies/{companyId}/employees")
    public Iterable<Employee> getEmployeesByCompanyId(@PathVariable(value = "companyId") UUID companyId) {
        return employeeService.getAllEmployeesByCompanyId(companyId);
    }

    @GetMapping("/employees/{employeeId}/cognito-status")
    public String getEmployeeCognitoStaus(@PathVariable(value = "employeeId") UUID employeeId) {
        return employeeService.getEmployeeCognitoStatus(employeeId);
    }
}

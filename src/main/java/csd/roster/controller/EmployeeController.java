package csd.roster.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import csd.roster.model.Employee;
import csd.roster.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {
    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/departments/{departmentId}/employees")
    public Employee addEmployee (@PathVariable(value = "departmentId") UUID departmentId,
                                 @Valid @RequestBody Employee employee) {
        return employeeService.addEmployee(departmentId, employee);
    }

    @PutMapping("/departments/{departmentId}/employees/{employeeId}")
    public Employee updateEmployee (@PathVariable(value = "departmentId") UUID departmentId,
                                    @PathVariable(value = "employeeId") UUID employeeId,
                                    @Valid @RequestBody Employee employee) {
        return employeeService.updateEmployee(departmentId, employeeId, employee);
    }

    @GetMapping("/departments/{departmentId}/employees/{employeeId}")
    public Employee getEmployee (@PathVariable(value = "departmentId") UUID departmentId,
                                    @PathVariable(value = "employeeId") UUID employeeId) {
        return employeeService.getEmployee(departmentId, employeeId);
    }

    @DeleteMapping("/departments/{departmentId}/employees/{employeeId}")
    public void deleteEmployee (@PathVariable(value = "departmentId") UUID departmentId,
                                 @PathVariable(value = "employeeId") UUID employeeId) {
        employeeService.deleteEmployee(departmentId, employeeId);
    }

    @GetMapping("companies/{companyId}/employees")
    public Iterable<Employee> getEmployeesByCompanyId(@PathVariable(value = "companyId") UUID companyId) {
        return employeeService.getAllEmployeesByCompanyId(companyId);
    }
}

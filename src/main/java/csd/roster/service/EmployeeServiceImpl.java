package csd.roster.service;

import java.util.List;
import java.util.UUID;

import csd.roster.model.Employee;
import csd.roster.model.WorkLocation;
import csd.roster.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private WorkLocationService workLocationService;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, WorkLocationService workLocationService) {
        this.employeeRepository = employeeRepository;
        this.workLocationService = workLocationService;
    }

    @Override
    public Employee addEmployee(UUID workLocationId, Employee employee) {
        // TODO: wait for workLocation updates to be merged to develop branch
//        WorkLocation workLocation = workLocationService.getWorkLocationById(workLocationId);
//        roster.setWorkLocation(workLocation);

        return employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployee(UUID workLocationId, UUID employeeId) {
        return null;
    }

    @Override
    public void deleteEmployee(UUID workLocationId, UUID employeeId) {

    }

    @Override
    public Employee updateEmployee(UUID workLocationId, UUID employeeId, Employee employee) {
        return null;
    }
}

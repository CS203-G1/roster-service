package csd.roster.service;

import java.util.List;
import java.util.UUID;

import csd.roster.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Override
    public Employee addEmployee(Employee employee) {
        return null;
    }

    @Override
    public Employee getEmployee(UUID employeeId) {
        return null;
    }

    @Override
    public void deleteEmployee(UUID employeeId) {

    }

    @Override
    public Employee updateEmployee(UUID employeeId, Employee employee) {
        return null;
    }
}

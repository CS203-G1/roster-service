package csd.roster.service;

import java.util.List;
import java.util.UUID;

import csd.roster.model.Department;
import csd.roster.model.Employee;
import csd.roster.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private DepartmentService departmentService;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, DepartmentService departmentService) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
    }

    @Override
    public Employee addEmployee(UUID companyId, UUID departmentId, Employee employee) {
        // TODO: wait for workLocation updates to be merged to develop branch
        Department department = departmentService.getDepartmentByIdAndCompanyId(departmentId, companyId);
        employee.setDepartment(department);

        return employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployee(UUID companyId, UUID departmentId, UUID employeeId) {
        return null;
    }

    @Override
    public void deleteEmployee(UUID companyId, UUID departmentId, UUID employeeId) {
        return null
    }

    @Override
    public Employee updateEmployee(UUID departmentId, UUID employeeId, Employee employee) {
        return null;
    }
}

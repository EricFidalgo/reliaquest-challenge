package com.challenge.api.services;

import com.challenge.api.dtos.CreateEmployeeRequestDto;
import com.challenge.api.dtos.EmployeeDto;
import com.challenge.api.mapper.EmployeeMapper;
import com.challenge.api.model.BasicEmployee;
import com.challenge.api.repository.EmployeeRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

// This class contains the business logic operations by separating the implementation from employee service
@Service
public class EmployeeServiceImplementation implements EmployeeService {

    // Handles anything related to the db
    private final EmployeeRepository employeeRepository;

    // The mapper converts between the database entities (BasicEmployee) and Dtos (EmployeeDto)
    private final EmployeeMapper employeeMapper;

    // This constructor uses employeeRepository and employeeMapper to give it the tools it needs
    // employee repository is the repository for accessing employee data
    // employee mapper is the mapper for converting between entity and Dto
    public EmployeeServiceImplementation(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    // Retrieves all employees from the db and converts them to Dtos
    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<BasicEmployee> employees = employeeRepository.findAll();
        return employees.stream().map(employeeMapper::toDto).collect(Collectors.toList());
    }

    // Creates an employee by finding by id and returns the employee mapper
    // If not found will return the empty optional
    @Override
    public Optional<EmployeeDto> getEmployeeByUuid(UUID uuid) {
        var employee = employeeRepository.findById(uuid);

        // throws an exception if the user uuid does not exist
        if (!employeeRepository.existsById(uuid)) {
            throw new IllegalArgumentException("Employee not found with id: " + uuid);
        }

        return employee.map(employeeMapper::toDto);
    }

    // Creates an employee based off of the employee request dto class
    @Override
    public EmployeeDto createEmployee(CreateEmployeeRequestDto employeeDto) {
        var employee = employeeMapper.toEntity(employeeDto);

        // Create a random uuid
        employee.setUuid(UUID.randomUUID());

        // Set the hire date to the current time
        employee.setContractHireDate(Instant.now());
        // The getContractTerminationDate is not implemented because the termination date is set when the employee gets
        // fired so this should be set to null
        // In the future an UPDATE request would be set and then you would be able to implement this
        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    // Updates an employee based off the UUID and the chosen employee
    @Override
    public EmployeeDto updateEmployee(CreateEmployeeRequestDto createEmployee, UUID uuid) {
        BasicEmployee employee = employeeRepository.findById(uuid).orElse(null);

        if (employee == null) {
            return null;
        } else {
            BasicEmployee updatedEmployee = employeeMapper.toEntity(createEmployee);
            updatedEmployee.setUuid(uuid);
            updatedEmployee.setContractTerminationDate(employee.getContractTerminationDate());
            return employeeMapper.toDto(employeeRepository.save(updatedEmployee));
        }
    }

    // Deletes the employee based off the UUID
    @Override
    public void deleteEmployee(UUID uuid) {

        // throws an exception if the user uuid does not exist
        if (!employeeRepository.existsById(uuid)) {
            throw new IllegalArgumentException("Employee not found with id: " + uuid);
        }
        employeeRepository.deleteById(uuid);
    }
}

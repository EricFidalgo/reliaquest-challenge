package com.challenge.api.services;

import com.challenge.api.dtos.CreateEmployeeRequestDto;
import com.challenge.api.dtos.EmployeeDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// This interface defines what the employee service implementation will have to do
public interface EmployeeService {

    List<EmployeeDto> getAllEmployees(); // retrieves the list of employees

    Optional<EmployeeDto> getEmployeeByUuid(
            UUID uuid); // retrieves the specific employee, if uuid is wrong it will return an empty optional

    EmployeeDto createEmployee(
            CreateEmployeeRequestDto
                    employeeDto); // creates the employee with variables from the mapper and the hire date and uuid

    EmployeeDto updateEmployee(CreateEmployeeRequestDto createEmployee, UUID uuid);

    void deleteEmployee(UUID uuid);

    List<EmployeeDto> createEmployees(List<CreateEmployeeRequestDto> employeeDtos);
}

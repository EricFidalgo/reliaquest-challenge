package com.challenge.api.mapper;

import com.challenge.api.dtos.CreateEmployeeRequestDto;
import com.challenge.api.dtos.EmployeeDto;
import com.challenge.api.model.BasicEmployee;
import com.challenge.api.model.Employee;
import org.springframework.stereotype.Component;

// Converts the Employee database entities into Employee Dtos
@Component
public class EmployeeMapper {
    // This Dto is for reading in employees
    public EmployeeDto toDto(Employee employee) {
        if (employee == null) {
            return null;
        }

        // the salary, age, and hire date are not here for security purposes
        EmployeeDto dto = new EmployeeDto();
        dto.setUuid(employee.getUuid());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setFullName(employee.getFullName());
        dto.setJobTitle(employee.getJobTitle());
        dto.setEmail(employee.getEmail());
        return dto;
    }

    // This Dto is for creating employees
    // The hire date and uuid are in the service implementation
    public BasicEmployee toEntity(CreateEmployeeRequestDto dto) {
        BasicEmployee employee = new BasicEmployee();
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setFullName(dto.getFirstName() + " " + dto.getLastName());
        employee.setSalary(dto.getSalary());
        employee.setAge(dto.getAge());
        employee.setJobTitle(dto.getJobTitle());
        employee.setEmail(dto.getEmail());

        return employee;
    }
}

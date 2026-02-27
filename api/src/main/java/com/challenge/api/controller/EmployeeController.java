package com.challenge.api.controller;

import com.challenge.api.dtos.CreateEmployeeRequestDto;
import com.challenge.api.dtos.EmployeeDto;
import com.challenge.api.services.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    // The employee service object is the mediator for CRUD operations for the employees
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // GET request to call all of the employees from the service layer
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // GET request to call a single employee by their uuid
    @GetMapping("/{uuid}")
    public ResponseEntity<EmployeeDto> getEmployeeByUuid(@PathVariable UUID uuid) {
        // service will return an optional
        Optional<EmployeeDto> employeeDtoOptional = employeeService.getEmployeeByUuid(uuid);

        if (employeeDtoOptional.isPresent()) {
            return ResponseEntity.ok(employeeDtoOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST creation by first checking if the employee request variables are valid and are not empty
    // if not valid then call Global exception handler
    // With the new createEmployees() we would still need this function for simplicity since the input of
    // createEmployees() takes a list of dicts
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody CreateEmployeeRequestDto createEmployee) {
        EmployeeDto createdEmployee = employeeService.createEmployee(createEmployee);

        // HttpStatus.CREATED will return a 201 message saying "Created"
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    // PUT request to update a user
    @PutMapping("/{uuid}")
    public ResponseEntity<EmployeeDto> updateEmployee(
            @Valid @RequestBody CreateEmployeeRequestDto createEmployee, @PathVariable UUID uuid) {
        EmployeeDto updatedEmployee = employeeService.updateEmployee(createEmployee, uuid);

        if (updatedEmployee == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(updatedEmployee);
        }
    }

    // DELETE to delete a user
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID uuid) {
        employeeService.deleteEmployee(uuid);
        return ResponseEntity.noContent().build();
    }

    // POST creation of a list of employees
    @PostMapping("/bulk")
    public ResponseEntity<List<EmployeeDto>> createEmployees(
            @Valid @RequestBody List<CreateEmployeeRequestDto> createEmployeeRequests) {

        List<EmployeeDto> createdEmployees = employeeService.createEmployees(createEmployeeRequests);

        return new ResponseEntity<>(createdEmployees, HttpStatus.CREATED);
    }
}

package com.challenge.api.services;

import com.challenge.api.dtos.CreateEmployeeRequestDto;
import com.challenge.api.dtos.EmployeeDto;
import com.challenge.api.mapper.EmployeeMapper;
import com.challenge.api.model.BasicEmployee;
import com.challenge.api.repository.EmployeeRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EmployeeServiceImplementation implements EmployeeService {

    // Handles anything related to the db
    private final EmployeeRepository employeeRepository;

    // The mapper converts between the database entities (BasicEmployee) and Dtos (EmployeeDto)
    private final EmployeeMapper employeeMapper;

    // Add a Web Client to send post notifications to an external API
    // This is the modern method compared to a response template where the variable uses polling
    private final WebClient webClient;

    // This constructor uses employeeRepository and employeeMapper to give it the tools it needs
    // employee repository is the repository for accessing employee data
    // employee mapper is the mapper for converting between entity and Dto
    public EmployeeServiceImplementation(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;

        // Building the web client with the url to send post notifications to
        // In the real world, the base url would be hidden in a seperate config file or a key, this is for security and for the ease of switching links in the future
        this.webClient = WebClient.builder()
                .baseUrl("https://webhook.site/4e19a834-c80a-4965-a485-64755f66fb30")
                .build();
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
        var savedEmployeeDto = employeeMapper.toDto(employeeRepository.save(employee));

        // Post the employee to the WebClient API
        this.webClient
                .post() // The definition for the HTTP Method
                .uri("/") // Uses the base url i wrote in the constructor (base url)
                .bodyValue(savedEmployeeDto) // This outputs the data that i want to write as a JSON
                .retrieve() // Execute the call
                .bodyToMono(Void.class) // We dont need to read the response body from the other server
                .subscribe(); // Fires our request to the url
        return savedEmployeeDto;
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

    // Creates a List of employees from the employees.json file
    @Override
    public List<EmployeeDto> createEmployees(List<CreateEmployeeRequestDto> employeeDtos) {

        List<BasicEmployee> employeesToSave = new ArrayList<>();

        // Loop through the list of the dto inputs and map each input and save it to the employeesToSave object
        for (var employee : employeeDtos) {
            var employeeMap = employeeMapper.toEntity(employee);
            employeeMap.setUuid(UUID.randomUUID());
            employeeMap.setContractHireDate(Instant.now());
            employeesToSave.add(employeeMap);
        }

        // Save all at once to repo using saveAll()
        // savedEmployee is here for data confirmation that it is inside of the db
        List<BasicEmployee> savedEmployee = employeeRepository.saveAll(employeesToSave);

        // Convert the saved entities back to DTOs to return to the user
        var savedEmployeeDtos =
                savedEmployee.stream().map(employeeMapper::toDto).collect(Collectors.toList());

        // Post the employee to the WebClient API
        this.webClient
                .post() // The definition for the HTTP Method
                .uri("/") // Uses the base url i wrote in the constructor (base url)
                .bodyValue(savedEmployeeDtos) // This outputs the data that i want to write as a JSON
                .retrieve() // Execute the call
                .bodyToMono(Void.class) // We dont need to read the response body from the other server
                .subscribe(); // Fires our request to the url

        return savedEmployeeDtos;
    }
}

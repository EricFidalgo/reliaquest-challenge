package com.challenge.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.challenge.api.controller.EmployeeController;
import com.challenge.api.dtos.CreateEmployeeRequestDto;
import com.challenge.api.dtos.EmployeeDto;
import com.challenge.api.services.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
public class EmployeeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeDto employeeDto;
    private UUID employeeUuid;

    @BeforeEach
    void setUp() {
        employeeUuid = UUID.randomUUID();
        employeeDto = new EmployeeDto();
        employeeDto.setUuid(employeeUuid);
        employeeDto.setFirstName("Eric");
        employeeDto.setLastName("Fidalgo");
        employeeDto.setFullName("Eric Fidalgo");
        employeeDto.setEmail("ericfidalgo.cs@gmail.com");
        employeeDto.setJobTitle("Software Engineer");
    }

    @Test
    void shouldGetAllEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of(employeeDto));

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uuid").value(employeeUuid.toString()))
                .andExpect(jsonPath("$[0].fullName").value("Eric Fidalgo"));
    }

    @Test
    void shouldGetEmployeeByUuid() throws Exception {
        when(employeeService.getEmployeeByUuid(employeeUuid)).thenReturn(Optional.of(employeeDto));

        mockMvc.perform(get("/api/v1/employees/{uuid}", employeeUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(employeeUuid.toString()))
                .andExpect(jsonPath("$.firstName").value("Eric"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentEmployee() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();
        when(employeeService.getEmployeeByUuid(nonExistentUuid)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/employees/{uuid}", nonExistentUuid)).andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateEmployee() throws Exception {
        CreateEmployeeRequestDto requestDto = new CreateEmployeeRequestDto();
        requestDto.setFirstName("Cool");
        requestDto.setLastName("Eric");
        requestDto.setEmail("cool.eric@gmail.com");
        requestDto.setJobTitle("Developer");
        requestDto.setSalary(50000);

        when(employeeService.createEmployee(any(CreateEmployeeRequestDto.class)))
                .thenReturn(employeeDto);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").exists());
    }

    @Test
    void shouldDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/api/v1/employees/{uuid}", employeeUuid)).andExpect(status().isNoContent());
    }

    @Test
    void shouldCreateEmployees() throws Exception {

        CreateEmployeeRequestDto emp1 = new CreateEmployeeRequestDto();
        emp1.setFirstName("Charlie");
        emp1.setLastName("Davis");
        emp1.setSalary(91000);
        emp1.setAge(42);
        emp1.setJobTitle("Senior Developer");
        emp1.setEmail("cdavis@example.com");

        CreateEmployeeRequestDto emp2 = new CreateEmployeeRequestDto();
        emp2.setFirstName("Bart");
        emp2.setLastName("Simpson");
        emp2.setSalary(2000);
        emp2.setAge(23);
        emp2.setJobTitle("Associate Developer");
        emp2.setEmail("bsimpson@example.com");

        List<CreateEmployeeRequestDto> employees = List.of(emp1, emp2);
        when(employeeService.createEmployees(anyList())).thenReturn(List.of(employeeDto));

        mockMvc.perform(post("/api/v1/employees/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employees)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isArray());
    }
}

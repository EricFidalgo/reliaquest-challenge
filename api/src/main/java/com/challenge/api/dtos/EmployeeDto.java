package com.challenge.api.dtos;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

// The Dtos main job is to send the employee information to the clients and to expose only what is needed for the user
// to see
@Getter
@Setter
public class EmployeeDto {

    private UUID uuid;
    private String firstName;
    private String lastName;
    private String fullName;
    private String jobTitle;
    private String email;
}

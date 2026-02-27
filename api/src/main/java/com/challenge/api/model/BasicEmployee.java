package com.challenge.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// This is the concrete implementation of the Employee interface which allows for loose coupling because we can
// implement different function methods with other classes
@Entity // JPA Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
public class BasicEmployee implements Employee {

    @Id
    private UUID uuid;

    private String firstName;
    private String lastName;
    private String fullName;
    private Integer salary;
    private Integer age;
    private String jobTitle;
    private String email;
    private Instant contractHireDate;
    private Instant contractTerminationDate;
}

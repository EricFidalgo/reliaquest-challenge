package com.challenge.api.repository;

import com.challenge.api.model.BasicEmployee;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Creates the interface for the database
@Repository
public interface EmployeeRepository extends JpaRepository<BasicEmployee, UUID> {}

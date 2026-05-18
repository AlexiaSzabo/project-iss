package org.example.repository;

import org.example.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    // LoginEmployeeSD - msg 13: employee = findByEmail(email, password)
    Optional<Employee> findByEmailAndPassword(String email, String password);
}
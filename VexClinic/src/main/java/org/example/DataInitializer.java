package org.example;

import org.example.model.Employee;
import org.example.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;

    public DataInitializer(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) {
        if (employeeRepository.count() == 0) {
            Employee admin = new Employee("Admin", "admin@vetclinic.com", "admin123", "ADMIN");
            employeeRepository.save(admin);
            System.out.println("✅ Default employee created: admin@vetclinic.com / admin123");
        }
    }
}
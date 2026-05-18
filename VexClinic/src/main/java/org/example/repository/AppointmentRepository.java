package org.example.repository;

import org.example.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    // findAll(), save(), deleteById() inherited from JpaRepository
}
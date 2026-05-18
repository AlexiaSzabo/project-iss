package org.example.repository;

import org.example.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Integer> {
    // findAll() is inherited from JpaRepository
    // save() is inherited from JpaRepository
}
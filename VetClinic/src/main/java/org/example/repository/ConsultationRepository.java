package org.example.repository;

import org.example.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Integer> {
    List<Consultation> findByAnimalIdOrderByDateDesc(int animalId);
}
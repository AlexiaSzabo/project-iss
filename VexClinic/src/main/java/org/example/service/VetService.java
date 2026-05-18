package org.example.service;

import org.example.model.*;
import org.example.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VetService {

    private final EmployeeRepository employeeRepository;
    private final AnimalRepository animalRepository;
    private final ClientRepository clientRepository;
    private final AppointmentRepository appointmentRepository;
    private final ConsultationRepository consultationRepository;
    private final TreatmentRepository treatmentRepository;
    private final VeterinarianRepository veterinarianRepository;

    public VetService(EmployeeRepository employeeRepository,
                      AnimalRepository animalRepository,
                      ClientRepository clientRepository,
                      AppointmentRepository appointmentRepository,
                      ConsultationRepository consultationRepository,
                      TreatmentRepository treatmentRepository,
                      VeterinarianRepository veterinarianRepository) {
        this.employeeRepository = employeeRepository;
        this.animalRepository = animalRepository;
        this.clientRepository = clientRepository;
        this.appointmentRepository = appointmentRepository;
        this.consultationRepository = consultationRepository;
        this.treatmentRepository = treatmentRepository;
        this.veterinarianRepository = veterinarianRepository;
    }

    // ── LOGIN ──────────────────────────────────────
    public Employee login(String email, String password) {
        return employeeRepository.findByEmailAndPassword(email, password).orElse(null);
    }

    // ── CLIENTS ────────────────────────────────────
    public boolean saveClient(String name, String phone, String email) {
        try {
            clientRepository.save(new Client(name, phone, email));
            return true;
        } catch (Exception e) { return false; }
    }

    public List<Client> getAllClients() { return clientRepository.findAll(); }

    public Optional<Client> getClientById(int id) { return clientRepository.findById(id); }

    public boolean updateClient(int id, String name, String phone, String email) {
        try {
            return clientRepository.findById(id).map(c -> {
                c.setName(name);
                c.setPhone(phone);
                c.setEmail(email);
                clientRepository.save(c);
                return true;
            }).orElse(false);
        } catch (Exception e) { return false; }
    }

    public boolean deleteClient(int id) {
        try {
            clientRepository.deleteById(id);
            return true;
        } catch (Exception e) { return false; }
    }

    // ── ANIMALS ────────────────────────────────────
    public boolean saveAnimal(String name, String species, String breed, int age, Integer clientId) {
        try {
            Animal animal = new Animal(name, species, breed, age);
            if (clientId != null) clientRepository.findById(clientId).ifPresent(animal::setClient);
            animalRepository.save(animal);
            return true;
        } catch (Exception e) { return false; }
    }

    public List<Animal> getAllAnimals() { return animalRepository.findAll(); }

    public Optional<Animal> getAnimalById(int id) { return animalRepository.findById(id); }

    public boolean updateAnimal(int id, String name, String species, String breed, int age, Integer clientId) {
        try {
            return animalRepository.findById(id).map(animal -> {
                animal.setName(name);
                animal.setSpecies(species);
                animal.setBreed(breed);
                animal.setAge(age);
                if (clientId != null) {
                    clientRepository.findById(clientId).ifPresent(animal::setClient);
                } else {
                    animal.setClient(null);
                }
                animalRepository.save(animal);
                return true;
            }).orElse(false);
        } catch (Exception e) { return false; }
    }

    public boolean deleteAnimal(int id) {
        try {
            animalRepository.deleteById(id);
            return true;
        } catch (Exception e) { return false; }
    }

    // ── APPOINTMENTS ───────────────────────────────
    public boolean saveAppointment(LocalDate date, String time, String reason,
                                   Integer animalId, Integer clientId, Integer veterinarianId) {
        try {
            Appointment appointment = new Appointment(date, time, reason, "Scheduled");
            if (animalId != null) animalRepository.findById(animalId).ifPresent(appointment::setAnimal);
            if (clientId != null) clientRepository.findById(clientId).ifPresent(appointment::setClient);
            if (veterinarianId != null) veterinarianRepository.findById(veterinarianId).ifPresent(appointment::setVeterinarian);
            appointmentRepository.save(appointment);
            return true;
        } catch (Exception e) { return false; }
    }

    public List<Appointment> getAllAppointments() { return appointmentRepository.findAll(); }

    public Optional<Appointment> getAppointmentById(int id) { return appointmentRepository.findById(id); }

    // UpdateAppointmentSD - msg 8
    public boolean updateAppointment(int id, LocalDate date, String time, String reason,
                                     Integer animalId, Integer clientId, Integer veterinarianId) {
        try {
            return appointmentRepository.findById(id).map(appt -> {
                appt.setDate(date);
                appt.setTime(time);
                appt.setReason(reason);
                if (animalId != null) animalRepository.findById(animalId).ifPresent(appt::setAnimal);
                if (clientId != null) clientRepository.findById(clientId).ifPresent(appt::setClient);
                if (veterinarianId != null) {
                    veterinarianRepository.findById(veterinarianId).ifPresent(appt::setVeterinarian);
                } else {
                    appt.setVeterinarian(null);
                }
                appointmentRepository.save(appt);
                return true;
            }).orElse(false);
        } catch (Exception e) { return false; }
    }

    // CancelAppointmentSD - msg 8
    public boolean cancelAppointment(int id) {
        try {
            return appointmentRepository.findById(id).map(appt -> {
                appt.setStatus("Cancelled");
                appointmentRepository.save(appt);
                return true;
            }).orElse(false);
        } catch (Exception e) { return false; }
    }

    // ── CONSULTATIONS ──────────────────────────────
    public boolean saveConsultation(int appointmentId, String symptoms, String diagnosis,
                                    String recommendations, double cost) {
        try {
            Optional<Appointment> apptOpt = appointmentRepository.findById(appointmentId);
            if (apptOpt.isEmpty()) return false;
            Appointment appt = apptOpt.get();

            Consultation consultation = new Consultation();
            consultation.setDate(LocalDate.now());
            consultation.setSymptoms(symptoms);
            consultation.setDiagnosis(diagnosis);
            consultation.setRecommendations(recommendations);
            consultation.setCost(cost);
            consultation.setAppointment(appt);
            consultation.setAnimal(appt.getAnimal());
            consultationRepository.save(consultation);

            appt.setStatus("Completed");
            appointmentRepository.save(appt);
            return true;
        } catch (Exception e) { return false; }
    }

    public Optional<Consultation> getConsultationById(int id) {
        return consultationRepository.findById(id);
    }

    public List<Consultation> getMedicalHistory(int animalId) {
        return consultationRepository.findByAnimalIdOrderByDateDesc(animalId);
    }

    // ── TREATMENTS ─────────────────────────────────
    public boolean saveTreatment(int consultationId, String name, String description,
                                 String dosage, String duration) {
        try {
            Optional<Consultation> consOpt = consultationRepository.findById(consultationId);
            if (consOpt.isEmpty()) return false;
            Treatment treatment = new Treatment();
            treatment.setName(name);
            treatment.setDescription(description);
            treatment.setDosage(dosage);
            treatment.setDuration(duration);
            treatment.setConsultation(consOpt.get());
            treatmentRepository.save(treatment);
            return true;
        } catch (Exception e) { return false; }
    }

    // ── VETERINARIANS (iter 3) ─────────────────────
    // ManageVeterinariansSD - saveVeterinarian()
    public boolean saveVeterinarian(String name, String specialization, String phone, String email) {
        try {
            veterinarianRepository.save(new Veterinarian(name, specialization, phone, email));
            return true;
        } catch (Exception e) { return false; }
    }

    public List<Veterinarian> getAllVeterinarians() { return veterinarianRepository.findAll(); }

    public Optional<Veterinarian> getVeterinarianById(int id) { return veterinarianRepository.findById(id); }

    // ManageVeterinariansSD - updateVeterinarian()
    public boolean updateVeterinarian(int id, String name, String specialization, String phone, String email) {
        try {
            return veterinarianRepository.findById(id).map(v -> {
                v.setName(name);
                v.setSpecialization(specialization);
                v.setPhone(phone);
                v.setEmail(email);
                veterinarianRepository.save(v);
                return true;
            }).orElse(false);
        } catch (Exception e) { return false; }
    }

    // ManageVeterinariansSD - deleteVeterinarian()
    public boolean deleteVeterinarian(int id) {
        try {
            veterinarianRepository.deleteById(id);
            return true;
        } catch (Exception e) { return false; }
    }

    // ── USERS / EMPLOYEES (iter 3) ─────────────────
    // ManageUsersSD - saveEmployee()
    public boolean saveEmployee(String name, String email, String password, String role) {
        try {
            employeeRepository.save(new Employee(name, email, password, role));
            return true;
        } catch (Exception e) { return false; }
    }

    public List<Employee> getAllEmployees() { return employeeRepository.findAll(); }

    public Optional<Employee> getEmployeeById(int id) { return employeeRepository.findById(id); }

    // ManageUsersSD - updateEmployee()
    public boolean updateEmployee(int id, String name, String email, String password, String role) {
        try {
            return employeeRepository.findById(id).map(e -> {
                e.setName(name);
                e.setEmail(email);
                if (password != null && !password.isBlank()) e.setPassword(password);
                e.setRole(role);
                employeeRepository.save(e);
                return true;
            }).orElse(false);
        } catch (Exception e) { return false; }
    }

    // ManageUsersSD - deleteEmployee()
    public boolean deleteEmployee(int id) {
        try {
            employeeRepository.deleteById(id);
            return true;
        } catch (Exception e) { return false; }
    }

    // ── REPORTS (iter 3) ───────────────────────────
    // GenerateReportsSD - getReportData()
    public Map<String, Object> getReportData() {
        Map<String, Object> data = new HashMap<>();

        List<Appointment> allAppts = appointmentRepository.findAll();
        long scheduled = allAppts.stream().filter(a -> "Scheduled".equals(a.getStatus())).count();
        long completed = allAppts.stream().filter(a -> "Completed".equals(a.getStatus())).count();
        long cancelled = allAppts.stream().filter(a -> "Cancelled".equals(a.getStatus())).count();

        List<Consultation> allCons = consultationRepository.findAll();
        double totalRevenue = allCons.stream().mapToDouble(Consultation::getCost).sum();

        // Most common species
        Map<String, Long> speciesCount = new HashMap<>();
        for (Animal a : animalRepository.findAll()) {
            speciesCount.merge(a.getSpecies(), 1L, Long::sum);
        }

        data.put("totalAnimals", animalRepository.count());
        data.put("totalClients", clientRepository.count());
        data.put("totalAppointments", allAppts.size());
        data.put("scheduledAppointments", scheduled);
        data.put("completedAppointments", completed);
        data.put("cancelledAppointments", cancelled);
        data.put("totalConsultations", allCons.size());
        data.put("totalRevenue", totalRevenue);
        data.put("totalVeterinarians", veterinarianRepository.count());
        data.put("totalUsers", employeeRepository.count());
        data.put("speciesCount", speciesCount);
        data.put("recentConsultations", allCons.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .limit(5).toList());

        return data;
    }
}
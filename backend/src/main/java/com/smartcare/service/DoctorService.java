package com.smartcare.service;

import com.smartcare.model.Appointment;
import com.smartcare.model.Doctor;
import com.smartcare.repository.AppointmentRepository;
import com.smartcare.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository       doctorRepository;
    private final AppointmentRepository  appointmentRepository;
    private final PasswordEncoder        passwordEncoder;

    /**
     * Returns available time slots for a doctor on a given date by subtracting
     * already-booked slots from the doctor's configured availableTimes list.
     *
     * @param doctorId the target doctor's ID
     * @param date     the requested date
     * @return list of free time strings, e.g. ["09:00", "11:00"]
     */
    public List<String> getAvailableSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + doctorId));

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.plusDays(1).atStartOfDay();

        List<String> bookedTimes = appointmentRepository
                .findByDoctorIdAndDate(doctorId, start, end)
                .stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString().substring(0, 5))
                .collect(Collectors.toList());

        return doctor.getAvailableTimes().stream()
                .filter(t -> !bookedTimes.contains(t))
                .collect(Collectors.toList());
    }

    /**
     * Validates doctor login credentials and returns a structured response map.
     *
     * @param email    the doctor's registered email
     * @param rawPassword plain-text password from the login request
     * @return map with "success" boolean and either "doctorId" or "error" key
     */
    public Map<String, Object> validateDoctorLogin(String email, String rawPassword) {
        return doctorRepository.findByUserEmail(email)
                .filter(d -> passwordEncoder.matches(rawPassword, d.getUser().getPassword()))
                .map(d -> Map.<String, Object>of(
                        "success",  true,
                        "doctorId", d.getId(),
                        "name",     d.getFullName()))
                .orElse(Map.of(
                        "success", false,
                        "error",   "Invalid email or password"));
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
}

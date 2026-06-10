package com.smartcare.service;

import com.smartcare.model.Appointment;
import com.smartcare.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    /**
     * Books (saves) a new appointment in the database.
     *
     * @param appointment the appointment entity to persist
     * @return the saved appointment with its generated ID
     */
    public Appointment bookAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    /**
     * Retrieves all appointments for a specific doctor on a given date.
     *
     * @param doctorId the ID of the doctor
     * @param date     the calendar date to filter by
     * @return list of appointments on that date
     */
    public List<Appointment> getAppointmentsForDoctorOnDate(Long doctorId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.plusDays(1).atStartOfDay();
        return appointmentRepository.findByDoctorIdAndDate(doctorId, start, end);
    }

    /**
     * Retrieves all appointments booked by a specific patient.
     */
    public List<Appointment> getAppointmentsForPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    /**
     * Retrieves all appointments for a doctor (all dates).
     */
    public List<Appointment> getAppointmentsForDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }
}

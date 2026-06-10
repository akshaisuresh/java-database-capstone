package com.smartcare.controller;

import com.smartcare.model.Appointment;
import com.smartcare.service.AppointmentService;
import com.smartcare.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final TokenService       tokenService;

    /** POST /api/appointments – book a new appointment */
    @PostMapping
    public ResponseEntity<?> book(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody Appointment appointment) {

        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }
        Appointment saved = appointmentService.bookAppointment(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /** GET /api/appointments/patient/{patientId} */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getByPatient(
            @PathVariable Long patientId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }
        List<Appointment> list = appointmentService.getAppointmentsForPatient(patientId);
        return ResponseEntity.ok(list);
    }

    /** GET /api/appointments/doctor/{doctorId} */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getByDoctor(
            @PathVariable Long doctorId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }
        List<Appointment> list = appointmentService.getAppointmentsForDoctor(doctorId);
        return ResponseEntity.ok(list);
    }

    private boolean isAuthorized(String authHeader) {
        return authHeader != null
                && authHeader.startsWith("Bearer ")
                && tokenService.validateToken(authHeader.substring(7));
    }
}

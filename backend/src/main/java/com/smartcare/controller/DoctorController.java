package com.smartcare.controller;

import com.smartcare.model.Doctor;
import com.smartcare.service.DoctorService;
import com.smartcare.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService  doctorService;
    private final TokenService   tokenService;

    /**
     * GET /api/doctors
     * Returns all registered doctors.
     */
    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    /**
     * GET /api/doctors/availability?doctorId=1&date=2025-06-10&specialization=Cardiology
     *
     * Exposes a GET endpoint for doctor availability using dynamic parameters.
     * Validates the Bearer token and returns a structured response using ResponseEntity.
     *
     * @param doctorId       optional doctor ID to check specific availability
     * @param date           the requested date (ISO format)
     * @param specialization optional filter by specialization
     * @param authHeader     Authorization header containing Bearer token
     */
    @GetMapping("/availability")
    public ResponseEntity<?> getDoctorAvailability(
            @RequestParam(required = false)                                   Long      doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)      LocalDate date,
            @RequestParam(required = false)                                   String    specialization,
            @RequestHeader(value = "Authorization", required = false)         String    authHeader) {

        // Validate Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);
        if (!tokenService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token is invalid or expired"));
        }

        // Return available slots for a specific doctor
        if (doctorId != null) {
            List<String> slots = doctorService.getAvailableSlots(doctorId, date);
            return ResponseEntity.ok(Map.of(
                    "doctorId",       doctorId,
                    "date",           date.toString(),
                    "availableSlots", slots));
        }

        // Return doctors filtered by specialization
        if (specialization != null) {
            List<Doctor> doctors = doctorService.getDoctorsBySpecialization(specialization);
            return ResponseEntity.ok(Map.of(
                    "specialization", specialization,
                    "date",           date.toString(),
                    "doctors",        doctors));
        }

        return ResponseEntity.badRequest()
                .body(Map.of("error", "Provide doctorId or specialization"));
    }

    /**
     * POST /api/doctors/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        Map<String, Object> result = doctorService.validateDoctorLogin(
                credentials.get("email"),
                credentials.get("password"));

        if (Boolean.TRUE.equals(result.get("success"))) {
            String token = tokenService.generateToken(credentials.get("email"));
            return ResponseEntity.ok(Map.of(
                    "token",    token,
                    "doctorId", result.get("doctorId"),
                    "name",     result.get("name")));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", result.get("error")));
    }
}

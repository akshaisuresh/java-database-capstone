package com.smartcare.controller;

import com.smartcare.model.Prescription;
import com.smartcare.service.PrescriptionService;
import com.smartcare.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final TokenService        tokenService;

    /**
     * POST /api/prescriptions
     *
     * Saves a prescription with token validation and request body validation.
     * Returns structured success or error messages using ResponseEntity.
     *
     * @param authHeader    Bearer JWT token in the Authorization header
     * @param prescription  validated request body
     */
    @PostMapping
    public ResponseEntity<?> createPrescription(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody Prescription prescription) {

        // Token validation
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", "Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);
        if (!tokenService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", "Token is invalid or expired"));
        }

        try {
            Prescription saved = prescriptionService.savePrescription(prescription);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "success",        true,
                            "message",        "Prescription saved successfully",
                            "prescriptionId", saved.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/prescriptions/patient/{patientId}
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getByPatient(
            @PathVariable Long patientId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ") ||
                !tokenService.validateToken(authHeader.substring(7))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", "Unauthorized"));
        }

        List<Prescription> prescriptions = prescriptionService.getByPatientId(patientId);
        return ResponseEntity.ok(Map.of("success", true, "data", prescriptions));
    }
}

package com.smartcare.controller;

import com.smartcare.model.Patient;
import com.smartcare.model.User;
import com.smartcare.repository.PatientRepository;
import com.smartcare.repository.UserRepository;
import com.smartcare.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository    userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder   passwordEncoder;
    private final TokenService      tokenService;

    /** POST /api/auth/login */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email    = credentials.get("email");
        String password = credentials.get("password");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }

        User   user  = userOpt.get();
        String token = tokenService.generateToken(email);

        if (user.getRole() == User.Role.PATIENT) {
            Long patientId = patientRepository.findByUserEmail(email)
                    .map(Patient::getId).orElse(null);
            return ResponseEntity.ok(Map.of(
                    "token",     token,
                    "role",      user.getRole(),
                    "patientId", patientId != null ? patientId : ""));
        }

        return ResponseEntity.ok(Map.of("token", token, "role", user.getRole()));
    }

    /** POST /api/auth/register (patient self-registration) */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        if (userRepository.findByEmail(body.get("email")).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        }

        User user = new User();
        user.setEmail(body.get("email"));
        user.setPassword(passwordEncoder.encode(body.get("password")));
        user.setRole(User.Role.PATIENT);
        userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(user);
        patient.setFullName(body.get("fullName"));
        patient.setPhone(body.get("phone"));
        patientRepository.save(patient);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Registration successful"));
    }
}

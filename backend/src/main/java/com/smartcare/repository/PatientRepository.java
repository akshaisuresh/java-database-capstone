package com.smartcare.repository;

import com.smartcare.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Retrieves a patient by their associated user email (derived query).
     */
    Optional<Patient> findByUserEmail(String email);

    /**
     * Retrieves a patient using either email or phone number.
     */
    @Query("SELECT p FROM Patient p WHERE p.user.email = :identifier OR p.phone = :identifier")
    Optional<Patient> findByEmailOrPhone(@Param("identifier") String identifier);
}

package com.smartcare.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    private String id;

    private Long appointmentId;
    private Long doctorId;
    private Long patientId;

    private LocalDateTime issuedAt = LocalDateTime.now();
    private String diagnosis;

    private List<Medication> medications;
    private String notes;
    private String followUpDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Medication {
        private String name;
        private String dosage;
        private String frequency;
        private String duration;
    }
}

# SmartCare Clinic Management System – Database Schema Design

## MySQL Schema (Relational Data)

### Table: `users`
Stores shared authentication and role info for all user types.

```sql
CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('ADMIN', 'DOCTOR', 'PATIENT') NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

### Table: `doctors`
Stores doctor-specific profile data.

```sql
CREATE TABLE doctors (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    full_name       VARCHAR(255) NOT NULL,
    specialization  VARCHAR(255) NOT NULL,
    phone           VARCHAR(20),
    available_times JSON,                        -- e.g. ["09:00","10:00","11:00"]
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

---

### Table: `patients`
Stores patient-specific profile data.

```sql
CREATE TABLE patients (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT NOT NULL UNIQUE,
    full_name    VARCHAR(255) NOT NULL,
    phone        VARCHAR(20),
    date_of_birth DATE,
    address      TEXT,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

---

### Table: `appointments`
Stores all scheduled appointments between patients and doctors.

```sql
CREATE TABLE appointments (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id        BIGINT NOT NULL,
    patient_id       BIGINT NOT NULL,
    appointment_time DATETIME NOT NULL,
    status           ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') DEFAULT 'PENDING',
    notes            TEXT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id)  REFERENCES doctors(id)  ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);
```

---

## Relationships Summary

| Relationship             | Type        | Foreign Key                         |
|--------------------------|-------------|-------------------------------------|
| users → doctors          | One-to-One  | doctors.user_id → users.id          |
| users → patients         | One-to-One  | patients.user_id → users.id         |
| doctors → appointments   | One-to-Many | appointments.doctor_id → doctors.id |
| patients → appointments  | One-to-Many | appointments.patient_id → patients.id|

---

## MongoDB Collection (Unstructured / Flexible Data)

### Collection: `prescriptions`
Stores flexible prescription documents linked to appointments.

```json
{
  "_id": "ObjectId",
  "appointmentId": 101,
  "doctorId": 5,
  "patientId": 12,
  "issuedAt": "2025-06-01T10:30:00",
  "diagnosis": "Hypertension",
  "medications": [
    {
      "name": "Amlodipine",
      "dosage": "5mg",
      "frequency": "Once daily",
      "duration": "30 days"
    },
    {
      "name": "Lisinopril",
      "dosage": "10mg",
      "frequency": "Once daily",
      "duration": "30 days"
    }
  ],
  "notes": "Patient advised to reduce salt intake.",
  "followUpDate": "2025-07-01"
}
```

---

## Stored Procedures

### `GetDailyAppointmentReportByDoctor`
```sql
DELIMITER $$
CREATE PROCEDURE GetDailyAppointmentReportByDoctor(IN report_date DATE)
BEGIN
    SELECT
        d.full_name   AS doctor_name,
        d.specialization,
        COUNT(a.id)   AS total_appointments
    FROM appointments a
    JOIN doctors d ON a.doctor_id = d.id
    WHERE DATE(a.appointment_time) = report_date
    GROUP BY d.id, d.full_name, d.specialization
    ORDER BY total_appointments DESC;
END$$
DELIMITER ;
```

### `GetDoctorWithMostPatientsByMonth`
```sql
DELIMITER $$
CREATE PROCEDURE GetDoctorWithMostPatientsByMonth(IN p_year INT, IN p_month INT)
BEGIN
    SELECT
        d.full_name        AS doctor_name,
        d.specialization,
        COUNT(DISTINCT a.patient_id) AS unique_patients
    FROM appointments a
    JOIN doctors d ON a.doctor_id = d.id
    WHERE YEAR(a.appointment_time)  = p_year
      AND MONTH(a.appointment_time) = p_month
    GROUP BY d.id
    ORDER BY unique_patients DESC
    LIMIT 1;
END$$
DELIMITER ;
```

### `GetDoctorWithMostPatientsByYear`
```sql
DELIMITER $$
CREATE PROCEDURE GetDoctorWithMostPatientsByYear(IN p_year INT)
BEGIN
    SELECT
        d.full_name        AS doctor_name,
        d.specialization,
        COUNT(DISTINCT a.patient_id) AS unique_patients
    FROM appointments a
    JOIN doctors d ON a.doctor_id = d.id
    WHERE YEAR(a.appointment_time) = p_year
    GROUP BY d.id
    ORDER BY unique_patients DESC
    LIMIT 1;
END$$
DELIMITER ;
```

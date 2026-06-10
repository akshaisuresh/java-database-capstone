# SmartCare Clinic Management System

A full-stack digital health platform built for **SmartCare Solutions**, enabling Admins, Doctors, and Patients to manage appointments, medical records, and prescriptions through role-specific portals.

---

## Tech Stack

| Layer      | Technology                          |
|------------|-------------------------------------|
| Frontend   | HTML5, CSS3, Vanilla JavaScript     |
| Backend    | Java 21, Spring Boot 3.2, Maven     |
| Relational DB | MySQL 8 (via Spring Data JPA)    |
| Document DB | MongoDB 7 (via Spring Data Mongo)  |
| Auth       | JWT (jjwt 0.11)                     |
| Container  | Docker, Docker Compose              |
| CI/CD      | GitHub Actions                      |

---

## Project Structure

```
smartcare/
├── backend/
│   ├── src/main/java/com/smartcare/
│   │   ├── controller/        # REST controllers
│   │   ├── service/           # Business logic
│   │   ├── repository/        # JPA + MongoDB repositories
│   │   ├── model/             # JPA entities + MongoDB documents
│   │   └── config/            # Security configuration
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── admin/                 # Admin portal (login + dashboard)
│   ├── doctor/                # Doctor portal (login + dashboard)
│   └── patient/               # Patient portal (login + dashboard)
├── .github/workflows/
│   └── ci.yml                 # GitHub Actions CI
├── schema-design.md           # MySQL + MongoDB schema
├── user-stories.md            # GitHub Issues / user stories
└── docker-compose.yml
```

---

## User Roles & Permissions

| Feature                         | Admin | Doctor | Patient |
|---------------------------------|:-----:|:------:|:-------:|
| Register / remove doctors       |  ✅   |        |         |
| View all appointments           |  ✅   |        |         |
| View own appointments           |       |  ✅    |  ✅     |
| Book an appointment             |       |        |  ✅     |
| Issue a prescription            |       |  ✅    |         |
| View own prescriptions          |       |  ✅    |  ✅     |
| Search doctors by name/spec     |       |        |  ✅     |
| View available slots            |       |  ✅    |  ✅     |
| Run stored procedure reports    |  ✅   |        |         |

---

## Quick Start

### Prerequisites
- Docker & Docker Compose installed

### Run the full stack
```bash
git clone <your-repo-url>
cd smartcare
docker-compose up --build
```

Backend available at: `http://localhost:8080`

### Run backend locally (without Docker)
```bash
cd backend
mvn spring-boot:run
```
Requires MySQL on port 3306 and MongoDB on port 27017.

---

## API Endpoints

### Auth
| Method | Endpoint              | Description              |
|--------|-----------------------|--------------------------|
| POST   | `/api/auth/login`     | Login (admin/patient)    |
| POST   | `/api/auth/register`  | Patient self-registration|

### Doctors
| Method | Endpoint                      | Description                        |
|--------|-------------------------------|------------------------------------|
| GET    | `/api/doctors`                | List all doctors                   |
| POST   | `/api/doctors/login`          | Doctor login                       |
| GET    | `/api/doctors/availability`   | Get available slots (auth required)|

### Appointments
| Method | Endpoint                          | Description                  |
|--------|-----------------------------------|------------------------------|
| POST   | `/api/appointments`               | Book appointment              |
| GET    | `/api/appointments/patient/{id}`  | Patient's appointments        |
| GET    | `/api/appointments/doctor/{id}`   | Doctor's appointments         |

### Prescriptions
| Method | Endpoint                           | Description              |
|--------|------------------------------------|--------------------------|
| POST   | `/api/prescriptions`               | Create prescription      |
| GET    | `/api/prescriptions/patient/{id}`  | Get patient prescriptions|

---

## Key SQL Commands

```sql
-- Show all tables
SHOW TABLES;

-- 5 sample patients
SELECT * FROM patients LIMIT 5;

-- Run stored procedures
CALL GetDailyAppointmentReportByDoctor('2025-06-10');
CALL GetDoctorWithMostPatientsByMonth(2025, 6);
CALL GetDoctorWithMostPatientsByYear(2025);
```

## Sample curl Commands

```bash
# Get all doctors
curl -X GET http://localhost:8080/api/doctors

# Login as doctor
TOKEN=$(curl -s -X POST http://localhost:8080/api/doctors/login \
  -H "Content-Type: application/json" \
  -d '{"email":"doctor@smartcare.com","password":"password123"}' | jq -r '.token')

# Get patient appointments (authenticated)
curl -X GET http://localhost:8080/api/appointments/patient/1 \
  -H "Authorization: Bearer $TOKEN"

# Get doctors by specialization and time
curl -X GET "http://localhost:8080/api/doctors/availability?specialization=Cardiology&date=2025-06-10" \
  -H "Authorization: Bearer $TOKEN"
```

---

## CI/CD

GitHub Actions workflow (`.github/workflows/ci.yml`) automatically:
1. Sets up Java 21 with Maven on every push/PR to `main` or `develop`
2. Compiles the Spring Boot application
3. Runs all unit tests
4. Packages the JAR
5. Builds the Docker image

---

## Database Design

See [`schema-design.md`](schema-design.md) for full MySQL table definitions, relationships, MongoDB collection structure, and stored procedures.

---

## Version Control

This project uses GitHub for version control:
- `main` — production-ready code
- `develop` — active development branch
- Feature branches: `feature/<name>`
- Issues track user stories (see `user-stories.md`)

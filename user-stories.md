# SmartCare – GitHub Issues: User Stories

## Admin User Stories

**Issue #1 – Admin: Register a new doctor**
> As an Admin, I want to register a new doctor in the system so that doctors can log in and manage their appointments.
- Acceptance: Admin fills in name, email, specialization, phone, available times, and password → doctor account created.

**Issue #2 – Admin: View all registered doctors**
> As an Admin, I want to view a list of all registered doctors so that I can manage the clinic's medical staff.
- Acceptance: Admin sees a table with name, email, specialization.

**Issue #3 – Admin: Remove a doctor from the system**
> As an Admin, I want to remove a doctor from the system so that former staff no longer have access.
- Acceptance: Doctor is deactivated and cannot log in.

**Issue #4 – Admin: View all patients**
> As an Admin, I want to see a list of all registered patients so that I can monitor clinic activity.

**Issue #5 – Admin: View all appointments**
> As an Admin, I want to view all appointments across all doctors so that I can generate reports.

**Issue #6 – Admin: Run daily appointment report**
> As an Admin, I want to run the GetDailyAppointmentReportByDoctor stored procedure so that I can see each doctor's workload for a given day.

**Issue #7 – Admin: Find busiest doctor by month**
> As an Admin, I want to run GetDoctorWithMostPatientsByMonth so that I can recognise top-performing doctors monthly.

**Issue #8 – Admin: Find busiest doctor by year**
> As an Admin, I want to run GetDoctorWithMostPatientsByYear so that I can assess annual performance.

---

## Doctor User Stories

**Issue #9 – Doctor: Log in to the portal**
> As a Doctor, I want to log in using my email and password so that I can securely access my appointment dashboard.
- Acceptance: Successful login returns a JWT token; invalid credentials return 401.

**Issue #10 – Doctor: View my appointments**
> As a Doctor, I want to view all my patient appointments so that I can plan my day effectively.
- Acceptance: Dashboard shows patient name, date/time, and status for all appointments.

**Issue #11 – Doctor: Filter appointments by date**
> As a Doctor, I want to filter my appointments by a specific date so that I can focus on today's schedule.

**Issue #12 – Doctor: View my available time slots**
> As a Doctor, I want to see which of my time slots are still available on a given date so that I know my remaining capacity.

**Issue #13 – Doctor: Issue a prescription**
> As a Doctor, I want to create a prescription for a patient after a consultation so that the patient has a record of their medication.
- Acceptance: Prescription is saved to MongoDB with diagnosis, medications, and follow-up date.

**Issue #14 – Doctor: View prescriptions I have issued**
> As a Doctor, I want to view all prescriptions I have written so that I can track my patients' treatment history.

---

## Patient User Stories

**Issue #15 – Patient: Register an account**
> As a Patient, I want to register with my email and password so that I can access the patient portal.

**Issue #16 – Patient: Log in to the portal**
> As a Patient, I want to log in using my email and password so that I can securely access my health information.

**Issue #17 – Patient: Search for a doctor by name**
> As a Patient, I want to search for a doctor by name so that I can find the right specialist quickly.
- Acceptance: Search results show doctor name, specialization, and a "Book Appointment" button.

**Issue #18 – Patient: Filter doctors by specialization**
> As a Patient, I want to filter doctors by specialization so that I can find a doctor relevant to my condition.

**Issue #19 – Patient: View a doctor's available slots**
> As a Patient, I want to see a doctor's available time slots for a given date so that I can choose a convenient appointment time.

**Issue #20 – Patient: Book an appointment**
> As a Patient, I want to book an appointment with a doctor at a specific time so that I can receive medical care.
- Acceptance: Appointment saved with status PENDING; doctor's slot becomes unavailable.

**Issue #21 – Patient: View my appointments**
> As a Patient, I want to view all my booked appointments so that I can keep track of my upcoming visits.
- Acceptance: List shows doctor name, specialization, date/time, and current status.

**Issue #22 – Patient: Cancel an appointment**
> As a Patient, I want to cancel an appointment so that the slot is freed for other patients.

**Issue #23 – Patient: View my prescriptions**
> As a Patient, I want to view prescriptions issued to me so that I know what medications I have been prescribed.

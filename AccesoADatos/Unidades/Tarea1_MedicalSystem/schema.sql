CREATE TABLE Patients (
                          patient_id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100),
                          date_of_birth DATE,
                          phone VARCHAR(20)
);

CREATE TABLE Doctors (
                         doctor_id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100),
                         specialization VARCHAR(100),
                         phone VARCHAR(20)
);

CREATE TABLE Medical_Records (
                                 record_id INT AUTO_INCREMENT PRIMARY KEY,
                                 patient_id INT,
                                 doctor_id INT,
                                 diagnosis TEXT,
                                 admission_date DATE,
                                 FOREIGN KEY (patient_id) REFERENCES Patients(patient_id),
                                 FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id)
);

CREATE TABLE Appointments (
                              appointment_id INT AUTO_INCREMENT PRIMARY KEY,
                              patient_id INT,
                              doctor_id INT,
                              appointment_date DATETIME,
                              FOREIGN KEY (patient_id) REFERENCES Patients(patient_id),
                              FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id)
);

CREATE TABLE Prescribed_Medications (
                                        prescription_id INT AUTO_INCREMENT PRIMARY KEY,
                                        record_id INT,
                                        medication_name VARCHAR(100),
                                        dosage VARCHAR(50),
                                        FOREIGN KEY (record_id) REFERENCES Medical_Records(record_id)
);

CREATE TABLE Patient_Payments (
                                  payment_id INT AUTO_INCREMENT PRIMARY KEY,
                                  patient_id INT,
                                  amount DECIMAL(10, 2),
                                  payment_date DATETIME,
                                  FOREIGN KEY (patient_id) REFERENCES Patients(patient_id)
);

CREATE TABLE User_Login (
                            user_id INT AUTO_INCREMENT PRIMARY KEY,
                            username VARCHAR(50) UNIQUE,
                            password VARCHAR(255),
                            patient_id INT NULL,
                            doctor_id INT NULL,
                            FOREIGN KEY (patient_id) REFERENCES Patients(patient_id),
                            FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id)
);


-- 1. Recuperar las citas de un paciente concreto con la fecha y los nombres del paciente y del médico
SELECT
    a.appointment_date,
    p.name AS patient_name,
    d.name AS doctor_name
FROM Appointments a
         JOIN Patients p ON a.patient_id = p.patient_id
         JOIN Doctors d ON a.doctor_id = d.doctor_id
WHERE p.patient_id = 1;

-- 2. Recuperar las citas de un paciente incluyendo el diagnóstico registrado en su historial médico (si existe)
SELECT
    a.appointment_date,
    p.name AS patient_name,
    d.name AS doctor_name,
    mr.diagnosis
FROM Appointments a
         JOIN Patients p ON a.patient_id = p.patient_id
         JOIN Doctors d ON a.doctor_id = d.doctor_id
         LEFT JOIN Medical_Records mr
                   ON mr.patient_id = a.patient_id AND mr.doctor_id = a.doctor_id
WHERE p.patient_id = 1;

-- 3. Identificar el nombre y la fecha de nacimiento de los dos pacientes más mayores
SELECT
    name,
    date_of_birth
FROM Patients
ORDER BY date_of_birth ASC
    LIMIT 2;

-- 4. Encontrar el nombre del paciente que más dinero ha gastado en el sistema
SELECT
    p.name AS patient_name,
    SUM(pay.amount) AS total_spent
FROM Patients p
         JOIN Patient_Payments pay ON p.patient_id = pay.patient_id
GROUP BY p.patient_id, p.name
ORDER BY total_spent DESC
    LIMIT 1;
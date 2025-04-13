package com.example.audiostreaming.controller;

import com.example.audiostreaming.entity.Student;
import com.example.audiostreaming.entity.Faculty;
import com.example.audiostreaming.repository.StudentRepository;
import com.example.audiostreaming.repository.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @PostMapping("/signup/student")
    public ResponseEntity<?> signupStudent(@RequestBody Student student) {
        try {
            String studentIdStr = String.valueOf(student.getStudentId());

            // Validate that student ID has exactly 10 digits
            if (!studentIdStr.matches("\\d{10}")) {
                return ResponseEntity.badRequest().body("Invalid Student ID. It must consist of exactly 10 digits.");
            }

            if (studentRepository.existsById(student.getStudentId())) {
                return ResponseEntity.badRequest().body("Student ID already taken!");
            }

            studentRepository.save(student);
            return ResponseEntity.status(HttpStatus.CREATED).body("Student registered successfully");
        } catch (Exception e) {
            System.err.println("Error during student signup: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during registration.");
        }
    }

    @PostMapping("/signup/faculty")
    public ResponseEntity<?> signupFaculty(@RequestBody Faculty faculty) {
        try {
            if (facultyRepository.existsById(faculty.getFacultyId())) {
                return ResponseEntity.badRequest().body("Faculty ID already taken!");
            }
            facultyRepository.save(faculty);
            return ResponseEntity.status(HttpStatus.CREATED).body("Faculty registered successfully");
        } catch (Exception e) {
            System.err.println("Error during faculty signup: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during registration.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String userIdStr = loginRequest.get("studentId");
            String password = loginRequest.get("password");

            if (userIdStr.length() == 4) {
                try {
                    Long facultyId = Long.parseLong(userIdStr);
                    Faculty faculty = facultyRepository.findById(facultyId).orElse(null);
                    if (faculty != null && faculty.getPassword().equals(password)) {
                        return ResponseEntity.ok(faculty);
                    }
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body("Invalid Faculty ID format.");
                }
            } else {
                try {
                    Long studentId = Long.parseLong(userIdStr);
                    Student student = studentRepository.findByStudentId(studentId).orElse(null);
                    if (student != null && student.getPassword().equals(password)) {
                        return ResponseEntity.ok(student);
                    }
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body("Invalid Student ID format.");
                }
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");

        } catch (Exception e) {
            System.err.println("Error during login: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login.");
        }
    }
}

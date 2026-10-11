
package com.stage.backend.controller;

import com.stage.backend.entity.Student;
import com.stage.backend.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository studentRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(
            @RequestBody Student student) {

        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email already registered"));
        }

        student.setPassword(
                passwordEncoder.encode(student.getPassword()));

        Student saved = studentRepository.save(student);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "id", saved.getId(),
                        "fullName", saved.getFullName(),
                        "email", saved.getEmail(),
                        "department", saved.getDepartment(),
                        "year", saved.getYear()
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginStudent(
            @RequestBody Map<String, String> credentials) {

        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Email and password are required"));
        }

        var studentOptional = studentRepository.findByEmail(email);

        if (studentOptional.isEmpty() ||
                !passwordEncoder.matches(
                        password,
                        studentOptional.get().getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message",
                            "Invalid email or password"));
        }

        Student student = studentOptional.get();

        return ResponseEntity.ok(
                Map.of(
                        "id", student.getId(),
                        "fullName", student.getFullName(),
                        "email", student.getEmail(),
                        "department", student.getDepartment(),
                        "year", student.getYear()
                )
        );
    }
}
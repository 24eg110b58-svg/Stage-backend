package com.stage.backend.controller;

import com.stage.backend.entity.Student;
import com.stage.backend.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getAllStudents() {
        return ResponseEntity.ok(
                studentRepository.findAll().stream()
                        .map(student -> Map.of(
                                "id", student.getId(),
                                "fullName", student.getFullName(),
                                "email", student.getEmail(),
                                "department", student.getDepartment() == null
                                        ? "" : student.getDepartment(),
                                "year", student.getYear() == null
                                        ? "" : student.getYear(),
                                "skills", student.getSkills() == null
                                        ? "" : student.getSkills(),
                                "achievements", student.getAchievements() == null
                                        ? "" : student.getAchievements()
                        ))
                        .toList()
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(
            @RequestBody Student student) {

        if (student.getEmail() == null ||
                student.getPassword() == null ||
                student.getEmail().isBlank() ||
                student.getPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Email and password are required"));
        }

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
                        "fullName", saved.getFullName() == null
                                ? "" : saved.getFullName(),
                        "email", saved.getEmail(),
                        "department", saved.getDepartment() == null
                                ? "" : saved.getDepartment(),
                        "year", saved.getYear() == null
                                ? "" : saved.getYear()
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
                        "fullName", student.getFullName() == null
                                ? "" : student.getFullName(),
                        "email", student.getEmail(),
                        "department", student.getDepartment() == null
                                ? "" : student.getDepartment(),
                        "year", student.getYear() == null
                                ? "" : student.getYear()
                )
        );
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {

        var studentOptional = studentRepository.findById(id);

        if (studentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Student not found"));
        }

        Student student = studentOptional.get();

        return ResponseEntity.ok(
                Map.of(
                        "id", student.getId(),
                        "fullName", student.getFullName() == null
                                ? "" : student.getFullName(),
                        "email", student.getEmail() == null
                                ? "" : student.getEmail(),
                        "department", student.getDepartment() == null
                                ? "" : student.getDepartment(),
                        "year", student.getYear() == null
                                ? "" : student.getYear(),
                        "skills", student.getSkills() == null
                                ? "" : student.getSkills(),
                        "achievements", student.getAchievements() == null
                                ? "" : student.getAchievements()
                )
        );
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<?> saveProfile(
            @PathVariable Long id,
            @RequestBody Student profileData) {

        var studentOptional = studentRepository.findById(id);

        if (studentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Student not found"));
        }

        Student student = studentOptional.get();

        if (profileData.getFullName() != null) {
            student.setFullName(profileData.getFullName());
        }

        if (profileData.getDepartment() != null) {
            student.setDepartment(profileData.getDepartment());
        }

        if (profileData.getYear() != null) {
            student.setYear(profileData.getYear());
        }

        student.setSkills(profileData.getSkills());
        student.setAchievements(profileData.getAchievements());

        Student saved = studentRepository.save(student);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Profile saved successfully",
                        "id", saved.getId(),
                        "fullName", saved.getFullName() == null
                                ? "" : saved.getFullName(),
                        "email", saved.getEmail() == null
                                ? "" : saved.getEmail(),
                        "department", saved.getDepartment() == null
                                ? "" : saved.getDepartment(),
                        "year", saved.getYear() == null
                                ? "" : saved.getYear(),
                        "skills", saved.getSkills() == null
                                ? "" : saved.getSkills(),
                        "achievements", saved.getAchievements() == null
                                ? "" : saved.getAchievements()
                )
        );
    }
}

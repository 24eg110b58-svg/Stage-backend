package com.stage.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String fullName;
private String email;

@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
private String password;

private String department;
private String year;

public Student() {
}

public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public String getFullName() {
    return fullName;
}

public void setFullName(String fullName) {
    this.fullName = fullName;
}

public String getEmail() {
    return email;
}

public void setEmail(String email) {
    this.email = email;
}

public String getPassword() {
    return password;
}

public void setPassword(String password) {
    this.password = password;
}

public String getDepartment() {
    return department;
}

public void setDepartment(String department) {
    this.department = department;
}

public String getYear() {
    return year;
}

public void setYear(String year) {
    this.year = year;
}
}

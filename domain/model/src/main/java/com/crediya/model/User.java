package com.crediya.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class User {
    private final Long id;
    private final String name;
    private final String lastName;
    private final String documentId;
    private final LocalDate birthDate;
    private final String address;
    private final String phoneNumber;
    private final String email;
    private final BigDecimal baseSalary;
    private final Role role;

    public User(Long id, String name, String lastName, String documentId, 
                LocalDate birthDate, String address, String phoneNumber, String email, 
                BigDecimal baseSalary, Role role) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.documentId = documentId;
        this.birthDate = birthDate;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.baseSalary = baseSalary;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", documentId='" + documentId + '\'' +
                ", birthDate=" + birthDate +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", baseSalary=" + baseSalary +
                ", role=" + role +
                '}';
    }
}
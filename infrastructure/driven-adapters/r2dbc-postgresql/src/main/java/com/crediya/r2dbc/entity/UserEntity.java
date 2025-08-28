package com.crediya.r2dbc.entity;

import com.crediya.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("users")
public class UserEntity {
    @Id
    private Long id;
    
    @Column("name")
    private String name;
    
    @Column("last_name")
    private String lastName;
    
    @Column("document_id")
    private String documentId;
    
    @Column("birth_date")
    private LocalDate birthDate;
    
    @Column("address")
    private String address;
    
    @Column("phone_number")
    private String phoneNumber;
    
    @Column("email")
    private String email;
    
    @Column("base_salary")
    private BigDecimal baseSalary;
    
    @Column("role")
    private Role role;
}
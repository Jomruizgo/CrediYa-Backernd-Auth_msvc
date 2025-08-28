package com.crediya.r2dbc.mapper;

import com.crediya.model.Role;
import com.crediya.model.User;
import com.crediya.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityMapperTest {

    private UserEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserEntityMapper.class);
    }

    @Test
    void shouldMapUserToEntity() {
        User user = new User(
            1L,
            "John",
            "Doe",
            LocalDate.of(1990, 5, 15),
            "123 Main St",
            "1234567890",
            "john.doe@email.com",
            new BigDecimal("2500000.00"),
            Role.CLIENT
        );

        UserEntity entity = mapper.toEntity(user);

        assertNotNull(entity);
        assertEquals(user.getId(), entity.getId());
        assertEquals(user.getName(), entity.getName());
        assertEquals(user.getLastName(), entity.getLastName());
        assertEquals(user.getBirthDate(), entity.getBirthDate());
        assertEquals(user.getAddress(), entity.getAddress());
        assertEquals(user.getPhoneNumber(), entity.getPhoneNumber());
        assertEquals(user.getEmail(), entity.getEmail());
        assertEquals(user.getBaseSalary(), entity.getBaseSalary());
        assertEquals(user.getRole(), entity.getRole());
    }

    @Test
    void shouldMapEntityToUser() {
        UserEntity entity = UserEntity.builder()
            .id(1L)
            .name("Jane")
            .lastName("Smith")
            .birthDate(LocalDate.of(1985, 8, 20))
            .address("456 Oak St")
            .phoneNumber("0987654321")
            .email("jane.smith@email.com")
            .baseSalary(new BigDecimal("3000000.00"))
            .role(Role.ADMIN)
            .build();

        User user = mapper.toDomain(entity);

        assertNotNull(user);
        assertEquals(entity.getId(), user.getId());
        assertEquals(entity.getName(), user.getName());
        assertEquals(entity.getLastName(), user.getLastName());
        assertEquals(entity.getBirthDate(), user.getBirthDate());
        assertEquals(entity.getAddress(), user.getAddress());
        assertEquals(entity.getPhoneNumber(), user.getPhoneNumber());
        assertEquals(entity.getEmail(), user.getEmail());
        assertEquals(entity.getBaseSalary(), user.getBaseSalary());
        assertEquals(entity.getRole(), user.getRole());
    }

    @Test
    void shouldMapUserWithNullFieldsToEntity() {
        User user = new User(
            null,
            "John",
            "Doe",
            null,
            null,
            null,
            "john.doe@email.com",
            new BigDecimal("2500000.00"),
            Role.CLIENT
        );

        UserEntity entity = mapper.toEntity(user);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals("John", entity.getName());
        assertEquals("Doe", entity.getLastName());
        assertNull(entity.getBirthDate());
        assertNull(entity.getAddress());
        assertNull(entity.getPhoneNumber());
        assertEquals("john.doe@email.com", entity.getEmail());
        assertEquals(new BigDecimal("2500000.00"), entity.getBaseSalary());
        assertEquals(Role.CLIENT, entity.getRole());
    }

    @Test
    void shouldMapEntityWithNullFieldsToUser() {
        UserEntity entity = UserEntity.builder()
            .id(null)
            .name("Jane")
            .lastName("Smith")
            .birthDate(null)
            .address(null)
            .phoneNumber(null)
            .email("jane.smith@email.com")
            .baseSalary(new BigDecimal("3000000.00"))
            .role(Role.ADMIN)
            .build();

        User user = mapper.toDomain(entity);

        assertNotNull(user);
        assertNull(user.getId());
        assertEquals("Jane", user.getName());
        assertEquals("Smith", user.getLastName());
        assertNull(user.getBirthDate());
        assertNull(user.getAddress());
        assertNull(user.getPhoneNumber());
        assertEquals("jane.smith@email.com", user.getEmail());
        assertEquals(new BigDecimal("3000000.00"), user.getBaseSalary());
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void shouldMapAllRoles() {
        for (Role role : Role.values()) {
            User user = new User(1L, "Test", "User", LocalDate.now(),
                               "Address", "Phone", "test@email.com",
                               new BigDecimal("2500000"), role);

            UserEntity entity = mapper.toEntity(user);
            assertEquals(role, entity.getRole());

            User mappedBack = mapper.toDomain(entity);
            assertEquals(role, mappedBack.getRole());
        }
    }

    @Test
    void shouldHandleRoundTripMapping() {
        User originalUser = new User(
            42L,
            "Original",
            "User",
            LocalDate.of(1992, 12, 25),
            "Original Address",
            "555-0123",
            "original@email.com",
            new BigDecimal("4500000.50"),
            Role.SELLER
        );

        UserEntity entity = mapper.toEntity(originalUser);
        User roundTripUser = mapper.toDomain(entity);

        assertEquals(originalUser.getId(), roundTripUser.getId());
        assertEquals(originalUser.getName(), roundTripUser.getName());
        assertEquals(originalUser.getLastName(), roundTripUser.getLastName());
        assertEquals(originalUser.getBirthDate(), roundTripUser.getBirthDate());
        assertEquals(originalUser.getAddress(), roundTripUser.getAddress());
        assertEquals(originalUser.getPhoneNumber(), roundTripUser.getPhoneNumber());
        assertEquals(originalUser.getEmail(), roundTripUser.getEmail());
        assertEquals(originalUser.getBaseSalary(), roundTripUser.getBaseSalary());
        assertEquals(originalUser.getRole(), roundTripUser.getRole());
    }
}
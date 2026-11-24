package com.infy.scm360.identity;

import com.infy.scm360.identity.domain.Role;
import com.infy.scm360.identity.domain.User;
import com.infy.scm360.identity.dto.LoginRequest;
import com.infy.scm360.identity.dto.LoginResponse;
import com.infy.scm360.identity.repository.RoleRepository;
import com.infy.scm360.identity.repository.UserRepository;
import com.infy.scm360.identity.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class IdentityServiceApplicationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role adminRole = roleRepository.save(new Role("r-admin", "ADMIN", "Administrator"));
        Role procRole = roleRepository.save(new Role("r-proc", "PROCUREMENT_MANAGER", "Procurement Manager"));

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("testadmin");
        user.setEmail("admin@test.com");
        user.setPasswordHash(passwordEncoder.encode("TestPass123!"));
        user.setFullName("Test Administrator");
        user.setStatus("ACTIVE");
        user.setCreatedAt(Instant.now());
        user.setCreatedBy("SYSTEM");
        user.setUpdatedAt(Instant.now());
        user.setUpdatedBy("SYSTEM");
        user.setRoles(Set.of(adminRole, procRole));

        userRepository.save(user);
    }

    @Test
    void testSuccessfulLogin() {
        LoginResponse response = authService.login(new LoginRequest("testadmin", "TestPass123!"), "127.0.0.1");

        assertNotNull(response);
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertEquals("testadmin", response.user().username());
        assertTrue(response.user().roles().contains("ADMIN"));
        assertTrue(response.user().roles().contains("PROCUREMENT_MANAGER"));
    }

    @Test
    void testFailedLoginAndLockout() {
        // 5 failed attempts
        for (int i = 1; i <= 4; i++) {
            assertThrows(Exception.class, () -> 
                authService.login(new LoginRequest("testadmin", "wrong_pass"), "127.0.0.1")
            );
        }

        // 5th attempt locks the account
        assertThrows(Exception.class, () -> 
            authService.login(new LoginRequest("testadmin", "wrong_pass"), "127.0.0.1")
        );

        User user = userRepository.findByUsername("testadmin").orElseThrow();
        assertTrue(user.isAccountLocked(), "User should be locked after 5 failed attempts");
    }
}

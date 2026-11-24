package com.infy.scm360.identity.service;

import com.infy.scm360.common.dto.PageResponse;
import com.infy.scm360.common.exception.ResourceNotFoundException;
import com.infy.scm360.common.exception.StateConflictException;
import com.infy.scm360.common.outbox.OutboxService;
import com.infy.scm360.common.security.SecurityUtils;
import com.infy.scm360.identity.domain.Role;
import com.infy.scm360.identity.domain.User;
import com.infy.scm360.identity.domain.UserLocation;
import com.infy.scm360.identity.dto.CreateUserRequest;
import com.infy.scm360.identity.dto.UpdateLocationsRequest;
import com.infy.scm360.identity.dto.UpdateRolesRequest;
import com.infy.scm360.identity.dto.UserResponse;
import com.infy.scm360.identity.repository.RoleRepository;
import com.infy.scm360.identity.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final OutboxService outboxService;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuditService auditService,
            OutboxService outboxService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
        this.outboxService = outboxService;
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return AuthService.mapToUserResponse(user);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> listUsers(Pageable pageable) {
        Page<User> page = userRepository.findAllByDeletedAtIsNull(pageable);
        return PageResponse.from(page.map(AuthService::mapToUserResponse));
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new StateConflictException("Username '" + request.username() + "' is already in use");
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new StateConflictException("Email '" + request.email() + "' is already registered");
        }

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setStatus("ACTIVE");
        user.setSupplierId(request.supplierId());
        user.setCreatedAt(Instant.now());
        user.setCreatedBy(SecurityUtils.getCurrentUserId());
        user.setUpdatedAt(Instant.now());
        user.setUpdatedBy(SecurityUtils.getCurrentUserId());

        if (request.roles() != null && !request.roles().isEmpty()) {
            Set<Role> roles = roleRepository.findByNameIn(request.roles());
            user.setRoles(roles);
        }

        if (request.locationIds() != null && !request.locationIds().isEmpty()) {
            Set<UserLocation> locations = new HashSet<>();
            for (String locId : request.locationIds()) {
                locations.add(new UserLocation(UUID.randomUUID().toString(), user, locId, Instant.now()));
            }
            user.setLocations(locations);
        }

        userRepository.save(user);
        auditService.record(SecurityUtils.getCurrentUserId(), "USER_CREATED", "Created user " + user.getUsername(), null);

        return AuthService.mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateRoles(String userId, UpdateRolesRequest request) {
        User user = userRepository.findById(userId)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Set<Role> roles = roleRepository.findByNameIn(request.roles());
        user.setRoles(roles);
        user.setUpdatedAt(Instant.now());
        user.setUpdatedBy(SecurityUtils.getCurrentUserId());

        userRepository.save(user);
        auditService.record(SecurityUtils.getCurrentUserId(), "ROLES_UPDATED", "Updated roles for user " + user.getUsername() + " to " + request.roles(), null);

        return AuthService.mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateLocations(String userId, UpdateLocationsRequest request) {
        User user = userRepository.findById(userId)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        user.getLocations().clear();
        if (request.locationIds() != null) {
            for (String locId : request.locationIds()) {
                user.getLocations().add(new UserLocation(UUID.randomUUID().toString(), user, locId, Instant.now()));
            }
        }
        user.setUpdatedAt(Instant.now());
        user.setUpdatedBy(SecurityUtils.getCurrentUserId());

        userRepository.save(user);
        auditService.record(SecurityUtils.getCurrentUserId(), "LOCATIONS_UPDATED", "Updated locations for user " + user.getUsername(), null);

        return AuthService.mapToUserResponse(user);
    }

    @Transactional
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        user.setDeletedAt(Instant.now());
        user.setStatus("INACTIVE");
        user.setUpdatedAt(Instant.now());
        user.setUpdatedBy(SecurityUtils.getCurrentUserId());

        userRepository.save(user);
        auditService.record(SecurityUtils.getCurrentUserId(), "USER_DELETED", "Soft deleted user " + user.getUsername(), null);
    }
}

package com.infy.scm360.identity.controller;

import com.infy.scm360.common.dto.PageResponse;
import com.infy.scm360.common.security.ScmUserPrincipal;
import com.infy.scm360.identity.dto.CreateUserRequest;
import com.infy.scm360.identity.dto.UpdateLocationsRequest;
import com.infy.scm360.identity.dto.UpdateRolesRequest;
import com.infy.scm360.identity.dto.UserResponse;
import com.infy.scm360.identity.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal ScmUserPrincipal principal) {
        return ResponseEntity.ok(userService.getUserById(principal.getUserId()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> listUsers(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.listUsers(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateRoles(
            @PathVariable String id,
            @Valid @RequestBody UpdateRolesRequest request) {
        return ResponseEntity.ok(userService.updateRoles(id, request));
    }

    @PutMapping("/{id}/locations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateLocations(
            @PathVariable String id,
            @Valid @RequestBody UpdateLocationsRequest request) {
        return ResponseEntity.ok(userService.updateLocations(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

package com.infy.scm360.identity.repository;

import com.infy.scm360.identity.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE (u.username = :input OR u.email = :input) AND u.deletedAt IS NULL")
    Optional<User> findByUsernameOrEmail(@Param("input") String input);

    Page<User> findAllByDeletedAtIsNull(Pageable pageable);
}

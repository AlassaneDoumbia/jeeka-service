package org.entrepreneurship.jeeka.repositories;

import org.entrepreneurship.jeeka.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<User, Long> {

    User findByEmailIgnoreCase(String email);
    User findByPhone(String phone);
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
    @Query(value = "SELECT COUNT(user_id) AS total_count FROM geotrac.user_roles WHERE role_id = 2",nativeQuery = true)
    int totalUser();

}

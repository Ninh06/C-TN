package com.example.cdtn.repository;

import com.example.cdtn.entity.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u " +
            "WHERE (:userName IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :userName, '%'))) " +
            "AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
            "AND (:fullName IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
            "AND (:userType IS NULL OR u.userType = :userType)")
    Page<User> searchUsers(@Param("userName") String userName,
                           @Param("email") String email,
                           @Param("fullName") String fullName,
                           @Param("userType") String userType,
                           Pageable pageable);

}

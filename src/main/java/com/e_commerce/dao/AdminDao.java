package com.e_commerce.dao;

import com.e_commerce.entity.Admin;
import com.e_commerce.entity.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminDao extends JpaRepository<Admin,Long> {
    List<Admin> findByIsActive(boolean b);
    Admin findByServiceNo(String serviceNo);
    Optional<Admin> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE Admin c SET c.isActive = :value where c.id = :adminId")
    int activateAdmin(
            @Param("adminId") Long adminId,
            @Param("value") boolean value
    );

    boolean existsByEmail(String email);

    boolean existsByRoles(Role role);
}

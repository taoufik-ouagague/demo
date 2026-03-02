package com.example.demo.repository;

import com.example.demo.entity.ERole;
import com.example.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
    Optional<Role> findByCode(String code);
    
    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.droits WHERE r.id = :id")
    Optional<Role> findByIdWithDroits(Integer id);
    
    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.droits")
    List<Role> findAllWithDroits();
    
    // Direct SQL: Check if role-droit association already exists
    @Query(value = "SELECT COUNT(*) > 0 FROM role_droit WHERE role_id = :roleId AND droit_id = :droitId", nativeQuery = true)
    boolean existsRoleDroit(Integer roleId, Integer droitId);
    
    // Direct SQL: Insert role-droit association without loading collection
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO role_droit (role_id, droit_id) VALUES (:roleId, :droitId) ON CONFLICT DO NOTHING", nativeQuery = true)
    int addDroitToRole(Integer roleId, Integer droitId);
}

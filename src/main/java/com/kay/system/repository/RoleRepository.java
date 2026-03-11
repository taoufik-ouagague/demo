package com.kay.system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kay.system.entity.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query(value = "SELECT * FROM roles WHERE code = :code", nativeQuery = true)
    Optional<Role> findByCode(@Param("code") String code);

    // Direct SQL: Check if role-droit association already exists
    @Query(value = "SELECT COUNT(*) > 0 FROM role_droit WHERE role_id = :roleId AND droit_id = :droitId", nativeQuery = true)
    boolean existsRoleDroit(Integer roleId, Integer droitId);

    // Direct SQL: Insert role-droit association without loading collection
    @Modifying
    @Transactional
    @Query(value = "INSERT IGNORE INTO role_droit (role_id, droit_id) VALUES (:roleId, :droitId)", nativeQuery = true)
    int addDroitToRole(Integer roleId, Integer droitId);

    // Direct SQL: Delete role-droit association
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM role_droit WHERE role_id = :roleId AND droit_id = :droitId", nativeQuery = true)
    int removeDroitFromRole(@Param("roleId") Integer roleId, @Param("droitId") Integer droitId);



    @Query("""
        SELECT r FROM Role r WHERE 
            (:libelle IS NULL OR LOWER(r.libelle) LIKE LOWER(CONCAT('%', :libelle, '%'))) AND 
            (:code    IS NULL OR LOWER(r.code)    LIKE LOWER(CONCAT('%', :code,    '%'))) AND 
            (:status  IS NULL OR r.status = :status)
            AND r.status not IN ('-1', '-2')
    """)
    Page<Role> searchRoles(
            @Param("libelle") String libelle,
            @Param("code") String code,
            @Param("status") String status,
            Pageable pageable);
}

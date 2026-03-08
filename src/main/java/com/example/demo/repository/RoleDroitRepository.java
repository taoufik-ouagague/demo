package com.example.demo.repository;

import com.example.demo.entity.RoleDroit;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleDroitRepository extends JpaRepository<RoleDroit, Integer> {

    @Query("""
                SELECT u FROM RoleDroit u
                WHERE u.role.id = :idRole
                AND u.status=:status
            """)
    List<RoleDroit> getRolesDroits(@Param("idRole") Integer idRole,
            @Param("status") String status);
    
}

package com.kay.system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kay.system.entity.Droit;

import java.util.Optional;

@Repository
public interface DroitRepository extends JpaRepository<Droit, Integer> {
    @Query(value = "SELECT * FROM droit WHERE code = :code", nativeQuery = true)
    Optional<Droit> findByCode(@Param("code") String code);
    
    @Query(value = "SELECT COUNT(*) > 0 FROM droit WHERE code = :code", nativeQuery = true)
    boolean existsByCode(@Param("code") String code);

    @Query(""" 
        SELECT d FROM Droit d WHERE 
       (:libelle IS NULL OR LOWER(d.libelle) LIKE LOWER(CONCAT('%', :libelle, '%'))) AND 
       (:code    IS NULL OR LOWER(d.code)    LIKE LOWER(CONCAT('%', :code,    '%'))) AND 
       (:status  IS NULL OR d.status = :status)
       AND d.status not IN ('-1', '-2')

       """)
Page<Droit> searchDroits(
    @Param("libelle") String libelle,
    @Param("code")    String code,
    @Param("status")  String status,
    Pageable pageable
);
}

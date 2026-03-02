package com.example.demo.repository;

import com.example.demo.entity.Droit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DroitRepository extends JpaRepository<Droit, Integer> {
    Optional<Droit> findByCode(String code);
    boolean existsByCode(String code);
}

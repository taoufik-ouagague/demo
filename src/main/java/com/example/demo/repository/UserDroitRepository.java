package com.example.demo.repository;

import com.example.demo.entity.Droit;
import com.example.demo.entity.User;
import com.example.demo.entity.UserDroit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDroitRepository extends JpaRepository<UserDroit, Integer> {
    List<UserDroit> findByUser(User user);
    List<UserDroit> findByUserAndStatus(User user, String status);
    Optional<UserDroit> findByUserAndDroit(User user, Droit droit);
    void deleteByUserAndDroit(User user, Droit droit);
    void deleteByUser(User user);
    
    /**
     * Check if a user has a specific droit/permission by code
     * @param user the user to check
     * @param droitCode the code of the droit to check for
     * @return true if user has the permission, false otherwise
     */
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
           "FROM user_droit ud " +
           "JOIN droit d ON ud.droit_id = d.id " +
           "WHERE ud.user_id = :userId AND d.code = :droitCode", nativeQuery = true)
    boolean existsByUserAndDroitCode(@Param("userId") Integer userId, @Param("droitCode") String droitCode);
}

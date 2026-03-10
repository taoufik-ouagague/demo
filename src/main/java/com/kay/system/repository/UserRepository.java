package com.kay.system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kay.system.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLogin(String login);

    Optional<User> findByEmail(String email);

    Boolean existsByLogin(String login);

    Boolean existsByEmail(String email);

    @Query("""
                SELECT u FROM User u
                WHERE (:libelle IS NULL OR :libelle = '' OR LOWER(u.libelle) LIKE LOWER(CONCAT('%', :libelle, '%')))
                  AND (:login IS NULL OR :login = '' OR LOWER(u.login) LIKE LOWER(CONCAT('%', :login, '%')))
                  AND (:status IS NULL OR u.status=:status)
                  AND u.status not IN ('-1', '-2')
            """)
    Page<User> searchUsers(@Param("libelle") String libelle,
            @Param("login") String login,
            @Param("status") String status,
            Pageable pageable);

}

package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.security.RequireAdminRole;
import com.example.demo.security.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DroitRepository droitRepository;

    @Autowired
    private UserDroitRepository userDroitRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==================== USER MANAGEMENT ====================

    /**
     * Enable or disable a user account
     * POST /api/admin/users/{userId}/status
     */
    @RequireAdminRole
    @PostMapping("/users/{userId}/status")
    public ResponseEntity<?> setUserStatus(@PathVariable Integer userId, @RequestBody Map<String, Boolean> request) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }

        User userToUpdate = user.get();
        userToUpdate.setStatus(request.get("status"));
        userRepository.save(userToUpdate);

        return ResponseEntity.ok(Map.of(
            "message", "Statut de l'utilisateur mis à jour avec succès",
            "userId", userId,
            "status", request.get("status")
        ));
    }

    /**
     * Reset user password to default
     * POST /api/admin/users/{userId}/reset-password
     * Resets password to initial default: Admin@123
     * Requires: USER_PASSWORD_RESET permission
     */
    @RequirePermission("USER_PASSWORD_RESET")
    @PostMapping("/users/{userId}/reset-password")
    public ResponseEntity<?> resetUserPassword(@PathVariable Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }

        // Reset to default password
        String defaultPassword = "Admin@123";
        User userToUpdate = user.get();
        userToUpdate.setPwd(passwordEncoder.encode(defaultPassword));
        userRepository.save(userToUpdate);

        return ResponseEntity.ok(Map.of(
            "message", "Mot de passe de l'utilisateur réinitialisé par défaut avec succès"
        ));
    }

    /**
     * Assign a role to a user
     * POST /api/admin/users/{userId}/assign-role/{roleId}
     */
    @RequireAdminRole
    @PostMapping("/users/{userId}/assign-role/{roleId}")
    public ResponseEntity<?> assignRoleToUser(@PathVariable Integer userId, @PathVariable Integer roleId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }

        Optional<Role> role = roleRepository.findById(roleId);
        if (!role.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rôle non trouvé"));
        }

        User userToUpdate = user.get();
        userToUpdate.setIdRole(roleId);
        userRepository.save(userToUpdate);

        return ResponseEntity.ok(Map.of(
            "message", "Rôle assigné à l'utilisateur avec succès",
            "userId", userId,
            "roleId", roleId,
            "roleName", role.get().getLibelle()
        ));
    }

    /**
     * Remove role from a user
     * POST /api/admin/users/{userId}/remove-role
     * Body: { "roleId": <roleId> }
     */
    @RequireAdminRole
    @PostMapping("/users/{userId}/remove-role")
    public ResponseEntity<?> removeRoleFromUser(@PathVariable Integer userId, @RequestBody Map<String, Integer> body) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }

        Integer roleId = body.get("roleId");
        if (roleId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "roleId est requis dans le corps de la requête"));
        }

        Optional<Role> role = roleRepository.findById(roleId);
        if (!role.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rôle non trouvé"));
        }

        User userToUpdate = user.get();
        
        // Check if user has this role
        if (!roleId.equals(userToUpdate.getIdRole())) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "L'utilisateur n'a pas ce rôle assigné",
                "userId", userId,
                "roleId", roleId,
                "currentRoleId", userToUpdate.getIdRole()
            ));
        }

        userToUpdate.setIdRole(null);
        userRepository.save(userToUpdate);

        return ResponseEntity.ok(Map.of(
            "message", "Rôle supprimé de l'utilisateur avec succès",
            "userId", userId,
            "roleId", roleId,
            "roleName", role.get().getCode()
        ));
    }

    // ==================== USER DROIT (PERMISSION) MANAGEMENT ====================

    /**
     * Assign a droit (permission) to a user
     * POST /api/admin/users/{userId}/assign-droit/{droitId}
     */
    @RequireAdminRole
    @PostMapping("/users/{userId}/assign-droit/{droitId}")
    public ResponseEntity<?> assignDroitToUser(@PathVariable Integer userId, @PathVariable Integer droitId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }

        Optional<Droit> droit = droitRepository.findById(droitId);
        if (!droit.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Droit non trouvé"));
        }

        Optional<UserDroit> existingUserDroit = userDroitRepository.findByUserAndDroit(user.get(), droit.get());
        if (existingUserDroit.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "L'utilisateur a déjà ce droit"));
        }

        UserDroit userDroit = new UserDroit(user.get(), droit.get());
        userDroitRepository.save(userDroit);

        return ResponseEntity.ok(Map.of(
            "message", "Droit assigné à l'utilisateur avec succès",
            "userId", userId,
            "droitId", droitId,
            "droitCode", droit.get().getCode(),
            "droitLibelle", droit.get().getLibelle()
        ));
    }

    /**
     * Remove a droit (permission) from a user
     * DELETE /api/admin/users/{userId}/remove-droit/{droitId}
     */
    @RequireAdminRole
    @DeleteMapping("/users/{userId}/remove-droit/{droitId}")
    public ResponseEntity<?> removeDroitFromUser(@PathVariable Integer userId, @PathVariable Integer droitId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }

        Optional<Droit> droit = droitRepository.findById(droitId);
        if (!droit.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Droit non trouvé"));
        }

        userDroitRepository.deleteByUserAndDroit(user.get(), droit.get());

        return ResponseEntity.ok(Map.of(
            "message", "Droit supprimé de l'utilisateur avec succès",
            "userId", userId,
            "droitId", droitId
        ));
    }

    /**
     * Get all droits for a user
     * GET /api/admin/users/{userId}/droits
     */
    @RequireAdminRole
    @GetMapping("/users/{userId}/droits")
    public ResponseEntity<?> getUserDroits(@PathVariable Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }

        List<UserDroit> userDroits = userDroitRepository.findByUserAndStatus(user.get(), true);

        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "login", user.get().getLogin(),
            "droits", userDroits
        ));
    }

    // ==================== ROLE MANAGEMENT ====================

    /**
     * Create a new role
     * POST /api/admin/roles
     */
    @RequireAdminRole
    @PostMapping("/roles")
    public ResponseEntity<?> createRole(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String libelle = request.get("libelle");
        String description = request.get("description");

        if (code == null || libelle == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Code et libelle sont requis"));
        }

        if (droitRepository.existsByCode(code)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Le code existe déjà"));
        }

        Role role = new Role();
        role.setCode(code);
        role.setLibelle(libelle);
        role.setDescription(description);

        Role savedRole = roleRepository.save(role);

        return ResponseEntity.ok(Map.of(
            "message", "Rôle créé avec succès",
            "role", savedRole
        ));
    }

    /**
     * Get all roles with their droits
     * GET /api/admin/roles
     */
    @RequireAdminRole
    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        List<Role> roles = roleRepository.findAllWithDroits();
        return ResponseEntity.ok(Map.of(
            "roles", roles
        ));
    }

    /**
     * Get a specific role with its droits
     * GET /api/admin/roles/{roleId}
     */
    @RequireAdminRole
    @GetMapping("/roles/{roleId}")
    public ResponseEntity<?> getRole(@PathVariable Integer roleId) {
        Optional<Role> role = roleRepository.findByIdWithDroits(roleId);
        if (!role.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rôle non trouvé"));
        }

        return ResponseEntity.ok(role.get());
    }

    /**
     * Assign a droit to a role
     * POST /api/admin/roles/{roleId}/assign-droit/{droitId}
     */
    @RequireAdminRole
    @PostMapping("/roles/{roleId}/assign-droit/{droitId}")
    public ResponseEntity<?> assignDroitToRole(@PathVariable Integer roleId, @PathVariable Integer droitId) {
        Optional<Role> role = roleRepository.findById(roleId);
        if (!role.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rôle non trouvé"));
        }

        Optional<Droit> droit = droitRepository.findById(droitId);
        if (!droit.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Droit non trouvé"));
        }

        Role roleToUpdate = role.get();
        if (!roleToUpdate.getDroits().contains(droit.get())) {
            roleToUpdate.getDroits().add(droit.get());
            roleRepository.save(roleToUpdate);
        }

        return ResponseEntity.ok(Map.of(
            "message", "Droit assigné au rôle avec succès",
            "roleId", roleId,
            "droitId", droitId
        ));
    }

    // ==================== DROIT MANAGEMENT ====================

    /**
     * Create a new droit (permission)
     * POST /api/admin/droits
     */
    @RequireAdminRole
    @PostMapping("/droits")
    public ResponseEntity<?> createDroit(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String libelle = request.get("libelle");
        String description = request.get("description");

        if (code == null || libelle == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Code et libelle sont requis"));
        }

        if (droitRepository.existsByCode(code)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Le code existe déjà"));
        }

        Droit droit = new Droit(code, libelle, description);
        Droit savedDroit = droitRepository.save(droit);

        return ResponseEntity.ok(Map.of(
            "message", "Droit créé avec succès",
            "droit", savedDroit
        ));
    }

    /**
     * Get all droits
     * GET /api/admin/droits
     */
    @RequireAdminRole
    @GetMapping("/droits")
    public ResponseEntity<?> getAllDroits() {
        List<Droit> droits = droitRepository.findAll();
        return ResponseEntity.ok(droits);
    }

    /**
     * Get a specific droit
     * GET /api/admin/droits/{droitId}
     */
    @RequireAdminRole
    @GetMapping("/droits/{droitId}")
    public ResponseEntity<?> getDroit(@PathVariable Integer droitId) {
        Optional<Droit> droit = droitRepository.findById(droitId);
        if (!droit.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Droit non trouvé"));
        }

        return ResponseEntity.ok(droit.get());
    }
}

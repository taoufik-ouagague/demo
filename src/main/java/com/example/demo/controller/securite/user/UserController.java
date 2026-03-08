package com.example.demo.controller.securite.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.User;
import com.example.demo.security.RequireAdminRole;
import com.example.demo.security.RequirePermission;
import com.example.demo.services.UserService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * GET /api/admin/ListUsers?page=0&size=10
     * Body: { "libelle", "login", "status" }
     */
    @PostMapping("/ListUsers")
    public ResponseEntity<?> listUsers(
            @RequestBody(required = false) UserBody body,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<User> users = userService.listUsers(body, page, size);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/admin/GetUser/{userId}
     */
    @RequireAdminRole
    @GetMapping("/GetUser/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Integer userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }
        return ResponseEntity.ok(user);
    }

    /**
     * POST /api/admin/AddUser
     * Body: { "login", "email", "libelle", "idRole" }
     */
    @RequireAdminRole
    @PostMapping("/AddUser")
    public ResponseEntity<?> saveUser(@RequestBody UserBody request) {
        String error = userService.createUser(request);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.ok(Map.of("message", "Utilisateur créé avec succès"));
    }

    /**
     * POST /api/admin/UpdateUser/{userId}
     * Body: { "email", "libelle" }
     */
    @RequireAdminRole
    @PostMapping("/UpdateUser/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> request
    ) {
        String error = userService.updateUser(userId, request.get("email"), request.get("libelle"));
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.ok(Map.of("message", "Utilisateur mis à jour avec succès"));
    }

    /**
     * POST /api/admin/DeleteUser/{userId}
     */
    @RequireAdminRole
    @PostMapping("/DeleteUser/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        boolean deleted = userService.deleteUser(userId);
        if (!deleted) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }
        return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès", "userId", userId));
    }

    /**
     * POST /api/admin/UpdateUserStatus/{userId}
     */
    @RequireAdminRole
    @PostMapping("/UpdateUserStatus/{userId}")
    public ResponseEntity<?> setUserStatus(
            @PathVariable Integer userId,
            @RequestBody Map<String, Boolean> request
    ) {
        Boolean active = userService.setUserStatus(userId, request.get("status"));
        if (active == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }
        return ResponseEntity.ok(Map.of("message", active ? "Utilisateur activé." : "Utilisateur désactivé"));
    }

    /**
     * POST /api/admin/ResetUserPassword/{userId}
     */
    @RequirePermission("USER_PASSWORD_RESET")
    @PostMapping("/ResetUserPassword/{userId}")
    public ResponseEntity<?> resetUserPassword(@PathVariable Integer userId) {
        boolean reset = userService.resetPassword(userId);
        if (!reset) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }
        return ResponseEntity.ok(Map.of("message", "Mot de passe de l'utilisateur a été réinitialisé."));
    }
}
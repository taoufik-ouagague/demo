package com.kay.system.controller.securite.user;

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

import com.kay.system.entity.User;
import com.kay.system.security.RequirePermission;
import com.kay.system.services.securite.user.IUserService;

@RestController
@RequestMapping("/api/securite")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * GET /api/securite/ListUsers?page=0&size=10
     * Body: { "libelle", "login", "status" }
     */

    @PostMapping("/ListUsers")
    @RequirePermission("USER_READ")
    public ResponseEntity<?> listUsers(
            @RequestBody(required = false) UserBody body,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> users = iUserService.listUsers(body, page, size);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/securite/GetUser/{userId}
     */
    @GetMapping("/GetUser/{userId}")
    @RequirePermission("USER_READ")
    public ResponseEntity<?> getUser(@PathVariable Integer userId) {
        User user = iUserService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }
        return ResponseEntity.ok(user);
    }

    /**
     * POST /api/securite/AddUser
     * Body: { "login", "email", "libelle", "idRole" }
     */
    @PostMapping("/AddUser")
    @RequirePermission("USER_CREATE")
    public ResponseEntity<?> saveUser(@RequestBody UserBody request) {
        String error = iUserService.createUser(request);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.ok(Map.of("message", "Utilisateur créé avec succès"));
    }

    /**
     * POST /api/securite/UpdateUser/{userId}
     * Body: { "email", "libelle" }
     */
    @PostMapping("/UpdateUser/{userId}")
    @RequirePermission("USER_UPDATE")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> request) {
        String error = iUserService.updateUser(userId, request.get("email"), request.get("libelle"));
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.ok(Map.of("message", "Utilisateur mis à jour avec succès"));
    }

    /**
     * POST /api/securite/DeleteUser/{userId}
     */
    @PostMapping("/DeleteUser/{userId}")
    @RequirePermission("USER_DELETE")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        boolean deleted = iUserService.deleteUser(userId);
        if (!deleted) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }
        return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès", "userId", userId));
    }

    /**
     * POST /api/securite/UpdateUserStatus/{userId}
     */
    @PostMapping("/UpdateUserStatus/{userId}")
    @RequirePermission("USER_UPDATE")
    public ResponseEntity<?> setUserStatus(
            @PathVariable Integer userId,
            @RequestBody Map<String, Boolean> request) {
        Boolean active = iUserService.setUserStatus(userId, request.get("status"));
        if (active == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }
        return ResponseEntity.ok(Map.of("message", active ? "Utilisateur activé." : "Utilisateur désactivé"));
    }

    /**
     * POST /api/securite/ResetUserPassword/{userId}
     */
    @RequirePermission("USER_PASSWORD_RESET")
    @PostMapping("/ResetUserPassword/{userId}")
    public ResponseEntity<?> resetUserPassword(@PathVariable Integer userId) {
        boolean reset = iUserService.resetPassword(userId);
        if (!reset) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }
        return ResponseEntity.ok(Map.of("message", "Mot de passe de l'utilisateur a été réinitialisé."));
    }
}
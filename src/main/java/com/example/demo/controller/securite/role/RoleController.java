package com.example.demo.controller.securite.role;

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

import com.example.demo.entity.Role;
import com.example.demo.security.RequireAdminRole;
import com.example.demo.services.RoleService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * POST /api/admin/ListRoles?page=0&size=10
     * Body: { "libelle", "code", "status" }
     */
    @RequireAdminRole
    @PostMapping("/ListRoles")
    public ResponseEntity<?> listRoles(
            @RequestBody(required = false) RoleBody body,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Role> roles = roleService.listRoles(body, page, size);
        return ResponseEntity.ok(roles);
    }

    /**
     * GET /api/admin/GetRole/{roleId}
     */
    @RequireAdminRole
    @GetMapping("/GetRole/{roleId}")
    public ResponseEntity<?> getRole(@PathVariable Integer roleId) {
        Role role = roleService.getRoleById(roleId);
        if (role == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rôle non trouvé"));
        }
        return ResponseEntity.ok(role);
    }

    /**
     * POST /api/admin/AddRole
     * Body: { "code", "libelle", "description" }
     */
    @RequireAdminRole
    @PostMapping("/AddRole")
    public ResponseEntity<?> saveRole(@RequestBody Map<String, String> request) {
        String error = roleService.createRole(request);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.ok(Map.of("message", "Rôle créé avec succès"));
    }

    /**
     * POST /api/admin/UpdateRole/{roleId}
     * Body: { "code", "libelle", "description" }
     */
    @RequireAdminRole
    @PostMapping("/UpdateRole/{roleId}")
    public ResponseEntity<?> updateRole(
            @PathVariable Integer roleId,
            @RequestBody Map<String, String> request
    ) {
        String error = roleService.updateRole(roleId, request);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.ok(Map.of("message", "Rôle mis à jour avec succès"));
    }

    /**
     * POST /api/admin/DeleteRole/{roleId}
     */
    @RequireAdminRole
    @PostMapping("/DeleteRole/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable Integer roleId) {
        boolean deleted = roleService.deleteRole(roleId);
        if (!deleted) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rôle non trouvé"));
        }
        return ResponseEntity.ok(Map.of("message", "Rôle supprimé avec succès", "roleId", roleId));
    }
}
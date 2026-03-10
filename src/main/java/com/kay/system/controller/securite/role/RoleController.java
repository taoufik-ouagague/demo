package com.kay.system.controller.securite.role;

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

import com.kay.system.entity.Role;
import com.kay.system.services.securite.role.IRoleService;

@RestController
@RequestMapping("/api/securite")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoleController {

    @Autowired
    private IRoleService IRoleService ;

    /**
     * POST /api/securite/ListRoles?page=0&size=10
     * Body: { "libelle", "code", "status" }
     */
    @PostMapping("/ListRoles")
    public ResponseEntity<?> listRoles(
            @RequestBody(required = false) RoleBody body,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Role> roles = IRoleService.listRoles(body, page, size);
        return ResponseEntity.ok(roles);
    }

    /**
     * GET /api/securite/GetRole/{roleId}
     */
    @GetMapping("/GetRole/{roleId}")
    public ResponseEntity<?> getRole(@PathVariable Integer roleId) {
        Role role = IRoleService.getRoleById(roleId);
        if (role == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rôle non trouvé"));
        }
        return ResponseEntity.ok(role);
    }

    /**
     * POST /api/securite/AddRole
     * Body: { "code", "libelle", "description" }
     */
    @PostMapping("/AddRole")
    public ResponseEntity<?> saveRole(@RequestBody Map<String, String> request) {
        String error = IRoleService.createRole(request);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.ok(Map.of("message", "Rôle créé avec succès"));
    }

    /**
     * POST /api/securite/UpdateRole/{roleId}
     * Body: { "code", "libelle", "description" }
     */
    @PostMapping("/UpdateRole/{roleId}")
    public ResponseEntity<?> updateRole(
            @PathVariable Integer roleId,
            @RequestBody Map<String, String> request
    ) {
        String error = IRoleService.updateRole(roleId, request);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.ok(Map.of("message", "Rôle mis à jour avec succès"));
    }

    /**
     * POST /api/securite/DeleteRole/{roleId}
     */
    @PostMapping("/DeleteRole/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable Integer roleId) {
        boolean deleted = IRoleService.deleteRole(roleId);
        if (!deleted) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rôle non trouvé"));
        }
        return ResponseEntity.ok(Map.of("message", "Rôle supprimé avec succès", "roleId", roleId));
    }
}
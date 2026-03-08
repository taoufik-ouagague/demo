package com.example.demo.controller.securite.droit;

import java.util.List;
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

import com.example.demo.entity.Droit;
import com.example.demo.security.RequireAdminRole;
import com.example.demo.security.RequirePermission;
import com.example.demo.services.DroitService;
import com.example.demo.services.DroitService.ManageRoleDroitsResult;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DroitController {

    @Autowired
    private DroitService droitService;

    // ==================== DROIT CRUD ====================

    /**
     * POST /api/admin/ListDroits?page=0&size=10
     * Body: { "libelle", "code", "status" }
     */
    @RequireAdminRole
    @PostMapping("/ListDroits")
    public ResponseEntity<?> listDroits(
            @RequestBody(required = false) DroitBody body,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Droit> droits = droitService.listDroits(body, page, size);
        return ResponseEntity.ok(droits);
    }

    /**
     * GET /api/admin/GetDroit/{droitId}
     */
    @RequireAdminRole
    @GetMapping("/GetDroit/{droitId}")
    public ResponseEntity<?> getDroit(@PathVariable Integer droitId) {
        Droit droit = droitService.getDroitById(droitId);
        if (droit == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Droit non trouvé"));
        }
        return ResponseEntity.ok(droit);
    }

    /**
     * POST /api/admin/AddDroit
     * Body: { "code", "libelle", "description" }
     */
    @RequireAdminRole
    @PostMapping("/AddDroit")
    public ResponseEntity<?> saveDroit(@RequestBody Map<String, String> request) {
        String error = droitService.createDroit(request);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.ok(Map.of("message", "Droit créé avec succès"));
    }

    /**
     * POST /api/admin/UpdateDroit/{droitId}
     * Body: { "code", "libelle", "description" }
     */
    @RequireAdminRole
    @PostMapping("/UpdateDroit/{droitId}")
    public ResponseEntity<?> updateDroit(
            @PathVariable Integer droitId,
            @RequestBody Map<String, String> request
    ) {
        String error = droitService.updateDroit(droitId, request);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.ok(Map.of("message", "Droit mis à jour avec succès"));
    }

    /**
     * POST /api/admin/DeleteDroit/{droitId}
     */
    @RequireAdminRole
    @PostMapping("/DeleteDroit/{droitId}")
    public ResponseEntity<?> deleteDroit(@PathVariable Integer droitId) {
        boolean deleted = droitService.deleteDroit(droitId);
        if (!deleted) {
            return ResponseEntity.badRequest().body(Map.of("message", "Droit non trouvé"));
        }
        return ResponseEntity.ok(Map.of("message", "Droit supprimé avec succès", "droitId", droitId));
    }

    // ==================== ROLE DROIT MANAGEMENT ====================

    /**
     * GET /api/admin/GetRoleDroits/{roleId}
     */
    @RequirePermission("ROLE_DROIT_MANAGEMENT")
    @GetMapping("/GetRoleDroits/{roleId}")
    public ResponseEntity<?> getRoleDroits(@PathVariable Integer roleId) {
        Map<String, Object> result = droitService.getRoleDroits(roleId);
        if (result == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rôle non trouvé"));
        }
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/admin/ManageRoleDroits/{roleId}
     * Body: { "action": "add" | "delete", "droitIds": [1, 2, 3] }
     */
    @RequirePermission("ROLE_DROIT_MANAGEMENT")
    @PostMapping("/ManageRoleDroits/{roleId}")
    public ResponseEntity<?> manageRoleDroits(
            @PathVariable Integer roleId,
            @RequestBody Map<String, Object> request
    ) {
        String action = (String) request.get("action");
        @SuppressWarnings("unchecked")
        List<Integer> droitIds = (List<Integer>) request.get("droitIds");

        ManageRoleDroitsResult result = droitService.manageRoleDroits(roleId, action, droitIds);

        if (result.hasError && result.processedDroitIds == null) {
            return ResponseEntity.badRequest().body(Map.of("message", result.message));
        }
        if (result.hasError) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", result.message,
                    "errors",  result.errors
            ));
        }

        return ResponseEntity.ok(Map.of(
                "message",           result.message,
                "action",            action,
                "roleId",            roleId,
                "processedDroitIds", result.processedDroitIds,
                "errors",            result.errors != null ? result.errors : List.of()
        ));
    }

    // ==================== USER DROIT MANAGEMENT ====================

    /**
     * POST /api/admin/AssignDroitToUser/{droitId}/{userId}
     */
    @RequireAdminRole
    @PostMapping("/AssignDroitToUser/{droitId}/{userId}")
    public ResponseEntity<?> assignDroitToUser(
            @PathVariable Integer userId,
            @PathVariable Integer droitId
    ) {
        String error = droitService.assignDroitToUser(userId, droitId);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.ok(Map.of(
                "message", "Droit assigné à l'utilisateur avec succès",
                "userId",  userId,
                "droitId", droitId
        ));
    }

    /**
     * POST /api/admin/RemoveDroitFromUser/{droitId}/{userId}
     */
    @RequireAdminRole
    @PostMapping("/RemoveDroitFromUser/{droitId}/{userId}")
    public ResponseEntity<?> removeDroitFromUser(
            @PathVariable Integer userId,
            @PathVariable Integer droitId
    ) {
        String error = droitService.removeDroitFromUser(userId, droitId);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.ok(Map.of(
                "message", "Droit supprimé de l'utilisateur avec succès",
                "userId",  userId,
                "droitId", droitId
        ));
    }

    /**
     * GET /api/admin/GetUserDroits/{userId}
     */
    @RequireAdminRole
    @GetMapping("/GetUserDroits/{userId}")
    public ResponseEntity<?> getUserDroits(@PathVariable Integer userId) {
        Map<String, Object> result = droitService.getUserDroits(userId);
        if (result == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }
        return ResponseEntity.ok(result);
    }
}
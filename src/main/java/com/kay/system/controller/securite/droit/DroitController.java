package com.kay.system.controller.securite.droit;

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

import com.kay.system.entity.Droit;

import com.kay.system.security.RequirePermission;
import com.kay.system.services.securite.droit.IDroitService;
import com.kay.system.services.securite.droit.DroitServicelmpl.ManageRoleDroitsResult;

@RestController
@RequestMapping("/api/securite")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DroitController {

    @Autowired
    private IDroitService IDroitService ;

    // ==================== DROIT CRUD ====================

    /**
     * POST /api/securite/ListDroits?page=0&size=10
     * Body: { "libelle", "code", "status" }
     */
    @PostMapping("/ListDroits")
    @RequirePermission("USER_READ")
    public ResponseEntity<?> listDroits(
            @RequestBody(required = false) DroitBody body,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Droit> droits = IDroitService.listDroits(body, page, size);
        return ResponseEntity.ok(droits);
    }

    /**
     * GET /api/securite/GetDroit/{droitId}
     */
    @GetMapping("/GetDroit/{droitId}")
    @RequirePermission("USER_READ")
    public ResponseEntity<?> getDroit(@PathVariable Integer droitId) {
        Droit droit = IDroitService.getDroitById(droitId);
        if (droit == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Droit non trouvé"));
        }
        return ResponseEntity.ok(droit);
    }

    /**
     * POST /api/securite/AddDroit
     * Body: { "code", "libelle", "description" }
     */
    @PostMapping("/AddDroit")
    @RequirePermission("USER_CREATE")
    public ResponseEntity<?> saveDroit(@RequestBody Map<String, String> request) {
        String error = IDroitService.createDroit(request);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.ok(Map.of("message", "Droit créé avec succès"));
    }

    /**
     * POST /api/securite/UpdateDroit/{droitId}
     * Body: { "code", "libelle", "description" }
     */
    @PostMapping("/UpdateDroit/{droitId}")
    @RequirePermission("USER_UPDATE")
    public ResponseEntity<?> updateDroit(
            @PathVariable Integer droitId,
            @RequestBody Map<String, String> request
    ) {
        String error = IDroitService.updateDroit(droitId, request);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.ok(Map.of("message", "Droit mis à jour avec succès"));
    }

    /**
     * POST /api/securite/DeleteDroit/{droitId}
     */
    @PostMapping("/DeleteDroit/{droitId}")
    @RequirePermission("USER_DELETE")
    public ResponseEntity<?> deleteDroit(@PathVariable Integer droitId) {
        boolean deleted = IDroitService.deleteDroit(droitId);
        if (!deleted) {
            return ResponseEntity.badRequest().body(Map.of("message", "Droit non trouvé"));
        }
        return ResponseEntity.ok(Map.of("message", "Droit supprimé avec succès", "droitId", droitId));
    }

    // ==================== ROLE DROIT MANAGEMENT ====================

    /**
     * GET /api/securite/GetRoleDroits/{roleId}
     */
    @RequirePermission("ROLE_DROIT_MANAGEMENT")
    @GetMapping("/GetRoleDroits/{roleId}")
    public ResponseEntity<?> getRoleDroits(@PathVariable Integer roleId) {
        Map<String, Object> result = IDroitService.getRoleDroits(roleId);
        if (result == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rôle non trouvé"));
        }
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/securite/ManageRoleDroits/{roleId}
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

        ManageRoleDroitsResult result = IDroitService.manageRoleDroits(roleId, action, droitIds);

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
     * POST /api/securite/AssignDroitToUser/{droitId}/{userId}
     */
    @PostMapping("/AssignDroitToUser/{droitId}/{userId}")
    @RequirePermission("USER_ASSIGN")
    public ResponseEntity<?> assignDroitToUser(
            @PathVariable Integer userId,
            @PathVariable Integer droitId
    ) {
        String error = IDroitService.assignDroitToUser(userId, droitId);
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
     * POST /api/securite/RemoveDroitFromUser/{droitId}/{userId}
     */
    @PostMapping("/RemoveDroitFromUser/{droitId}/{userId}")
    @RequirePermission("USER_ASSIGN")
    public ResponseEntity<?> removeDroitFromUser(
            @PathVariable Integer userId,
            @PathVariable Integer droitId
    ) {
        String error = IDroitService.removeDroitFromUser(userId, droitId);
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
     * GET /api/securite/GetUserDroits/{userId}
     */
    @GetMapping("/GetUserDroits/{userId}")
    @RequirePermission("USER_READ")
    public ResponseEntity<?> getUserDroits(@PathVariable Integer userId) {
        Map<String, Object> result = IDroitService.getUserDroits(userId);
        if (result == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }
        return ResponseEntity.ok(result);
    }
}
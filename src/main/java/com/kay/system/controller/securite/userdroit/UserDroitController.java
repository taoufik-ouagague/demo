package com.kay.system.controller.securite.userdroit;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kay.system.entity.Droit.DroitIdsRequest;
import com.kay.system.security.RequirePermission;
import com.kay.system.services.securite.userdroit.IUserDroitService;

@RestController
@RequestMapping("/api/securite")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserDroitController {

    @Autowired
    private IUserDroitService userDroitService;


    // GET all droits (assigned + unassigned) for a user
    // GET /api/securite/GetAllDroitsForUser/{userId}
     @RequirePermission("ROLE_SECURITE")
    @GetMapping("/GetAllDroitsForUser/{userId}")
    public ResponseEntity<?> getAllDroitsForUser(@PathVariable Integer userId) {

        Map<String, Object> result = userDroitService.getAllDroitsForUser(userId);

        if (result == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Utilisateur introuvable "));
        }

        return ResponseEntity.ok(result);
    }

    // ASSIGN multiple droits to a user (bulk assignment)
    // POST /api/securite/AssignDroitsToUser/{userId}
    // Request body: { "droitIds": [1, 2, 3] }
    @RequirePermission("ROLE_SECURITE")
    @PostMapping("/AssignDroitsToUser/{userId}")
    public ResponseEntity<?> assignDroitsToUser(
            @PathVariable Integer userId,
            @RequestBody DroitIdsRequest request) {
        
        List<Integer> droitIds = request.getDroitIds();
        if (droitIds == null || droitIds.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Aucun droit sélectionné"));
        }

        int assignedCount = 0;
        int failedCount = 0;
        
        for (Integer droitId : droitIds) {
            String error = userDroitService.assignDroitToUser(userId, droitId);
            if (error != null) {
                failedCount++;
            } else {
                assignedCount++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("message", String.format("%d droit(s) assigné(s) avec succès", assignedCount));
        if (failedCount > 0) {
            result.put("warning", String.format("%d droit(s) n'ont pas pu être assignés", failedCount));
        }
        return ResponseEntity.ok(result);
    }

    // REMOVE a droit from a user → returns refreshed both lists
    // POST /api/securite/RemoveDroitFromUser/{droitId}/{userId}

    @RequirePermission("ROLE_SECURITE")
    @PostMapping("/RemoveDroitFromUser/{droitId}/{userId}")
    public ResponseEntity<?> removeDroitFromUser(
            @PathVariable Integer userId,
            @PathVariable Integer droitId) {

        String error = userDroitService.removeDroitFromUser(userId, droitId);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }

        // Return the refreshed lists immediately after removal
        Map<String, Object> result = userDroitService.getAllDroitsForUser(userId);
        result.put("message", "Droit supprimé de l'utilisateur avec succès");
        return ResponseEntity.ok(result);
    }

    // REMOVE multiple droits from a user (bulk removal)
    // POST /api/securite/RemoveDroitsFromUser/{userId}
    // Request body: { "droitIds": [1, 2, 3] }

    @RequirePermission("ROLE_SECURITE")
    @PostMapping("/RemoveDroitsFromUser/{userId}")
    public ResponseEntity<?> removeDroitsFromUser(
            @PathVariable Integer userId,
            @RequestBody DroitIdsRequest request) {
        
        List<Integer> droitIds = request.getDroitIds();
        if (droitIds == null || droitIds.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Aucun droit sélectionné"));
        }

        int removedCount = 0;
        int failedCount = 0;
        
        for (Integer droitId : droitIds) {
            String error = userDroitService.removeDroitFromUser(userId, droitId);
            if (error != null) {
                failedCount++;
            } else {
                removedCount++;
            }
        }

        // Return the refreshed lists immediately after removal
        Map<String, Object> result = userDroitService.getAllDroitsForUser(userId);
        result.put("message", String.format("%d droit(s) supprimé(s) avec succès", removedCount));
        if (failedCount > 0) {
            result.put("warning", String.format("%d droit(s) n'ont pas pu être supprimés", failedCount));
        }
        return ResponseEntity.ok(result);
    }
}
package com.kay.system.controller.securite.droit;

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

import com.kay.system.services.securite.droit.IDroitService;

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
    public ResponseEntity<?> deleteDroit(@PathVariable Integer droitId) {
        boolean deleted = IDroitService.deleteDroit(droitId);
        if (!deleted) {
            return ResponseEntity.badRequest().body(Map.of("message", "Droit non trouvé"));
        }
        return ResponseEntity.ok(Map.of("message", "Droit supprimé avec succès", "droitId", droitId));
    }
}
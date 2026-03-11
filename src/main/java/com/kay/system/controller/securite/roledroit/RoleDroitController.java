
package com.kay.system.controller.securite.roledroit;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kay.system.services.securite.roledroit.IRoleDroitService;
import com.kay.system.services.securite.roledroit.RoleDroitServicelmpl.ManageRoleDroitsResult;

@RestController
@RequestMapping("/api/securite")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoleDroitController {

    @Autowired
    private IRoleDroitService roleDroitService;   // ← own service, not IDroitService

    @GetMapping("/GetRoleDroits/{roleId}")
    public ResponseEntity<?> getRoleDroits(@PathVariable Integer roleId) {
        Map<String, Object> result = roleDroitService.getRoleDroits(roleId);
        if (result == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rôle non trouvé"));
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/ManageRoleDroits/{roleId}")
    public ResponseEntity<?> manageRoleDroits(
            @PathVariable Integer roleId,
            @RequestBody   RoleDroitBody body
    ) {
        String       action   = body.getAction();
        List<Integer> droitIds = body.getDroitIds();

        ManageRoleDroitsResult result = roleDroitService.manageRoleDroits(roleId, action, droitIds);

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
}
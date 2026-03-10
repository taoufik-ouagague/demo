package com.kay.system.services.securite.droit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kay.system.constants.GlobalConstants;
import com.kay.system.controller.securite.droit.DroitBody;
import com.kay.system.entity.Droit;
import com.kay.system.entity.Role;
import com.kay.system.entity.User;
import com.kay.system.entity.UserDroit;
import com.kay.system.repository.DroitRepository;
import com.kay.system.repository.RoleRepository;
import com.kay.system.repository.UserDroitRepository;
import com.kay.system.repository.UserRepository;

@Service
public class DroitServicelmpl implements IDroitService {

    @Autowired
    private DroitRepository droitRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDroitRepository userDroitRepository;

    // ==================== DROIT CRUD ====================
    @Transactional
    public Page<Droit> listDroits(DroitBody filters, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return droitRepository.searchDroits(
                filters.getLibelle(),
                filters.getCode(),
                filters.getStatus(),
                pageable
        );
    }


    @Transactional
    public Droit getDroitById(Integer droitId) {
        return droitRepository.findById(droitId).orElse(null);
    }


    @Transactional
    public String createDroit(Map<String, String> request) {
        String code        = request.get("code");
        String libelle     = request.get("libelle");
        String description = request.get("description");

        if (code == null || libelle == null) {
            return "Code et libelle sont requis";
        }

        boolean codeExists = droitRepository.findAll().stream()
                .anyMatch(d -> d.getCode() != null && d.getCode().equals(code));
        if (codeExists) {
            return "Le code existe déjà";
        }

        Droit droit = new Droit(code, libelle, description);
        droit.setStatus(GlobalConstants.STATUT_ACTIF);
        droitRepository.save(droit);

        return null; // null means success
    }


    @Transactional
    public String updateDroit(Integer droitId, Map<String, String> request) {
        Droit droit = droitRepository.findById(droitId).orElse(null);
        if (droit == null) {
            return "Droit non trouvé";
        }

        String code = request.get("code");
        if (code != null && !code.isEmpty()) {
            boolean codeExists = droitRepository.findAll().stream()
                    .anyMatch(d -> d.getCode() != null && d.getCode().equals(code) && !d.getId().equals(droitId));
            if (codeExists) {
                return "Le code existe déjà";
            }
            droit.setCode(code);
        }

        String libelle = request.get("libelle");
        if (libelle != null && !libelle.isEmpty()) {
            droit.setLibelle(libelle);
        }

        String description = request.get("description");
        if (description != null) {
            droit.setDescription(description);
        }

        droitRepository.save(droit);
        return null; // null means success
    }

    @Transactional      
    public boolean deleteDroit(Integer droitId) {
        Droit droit = droitRepository.findById(droitId).orElse(null);
        if (droit == null) return false;
        droit.setStatus(GlobalConstants.STATUT_DELETE);
        droitRepository.save(droit);
        return true;
    }

    // ==================== ROLE DROIT MANAGEMENT ====================

    @Transactional
    public Map<String, Object> getRoleDroits(Integer roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) return null;

        List<Droit> allDroits = droitRepository.findAll();

        List<Droit> assignedDroits = allDroits.stream()
                .filter(d -> roleRepository.existsRoleDroit(roleId, d.getId()))
                .collect(Collectors.toList());

        List<Droit> notAssignedDroits = allDroits.stream()
                .filter(d -> !roleRepository.existsRoleDroit(roleId, d.getId()))
                .collect(Collectors.toList());

        return Map.of(
                "roleId",            roleId,
                "roleName",          role.getLibelle(),
                "assignedDroits",    assignedDroits,
                "notAssignedDroits", notAssignedDroits,
                "totalAssigned",     assignedDroits.size(),
                "totalNotAssigned",  notAssignedDroits.size()
        );
    }
    @Transactional
    public static class ManageRoleDroitsResult {
        public final String        message;
        public final List<Integer> processedDroitIds;
        public final List<String>  errors;
        public final boolean       hasError;

        public ManageRoleDroitsResult(String message, List<Integer> processedDroitIds, List<String> errors, boolean hasError) {
            this.message           = message;
            this.processedDroitIds = processedDroitIds;
            this.errors            = errors;
            this.hasError          = hasError;
        }
    }

    @Transactional
    public ManageRoleDroitsResult manageRoleDroits(Integer roleId, String action, List<Integer> droitIds) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            return new ManageRoleDroitsResult("Rôle non trouvé", null, null, true);
        }
        if (action == null || (!action.equals("add") && !action.equals("delete"))) {
            return new ManageRoleDroitsResult("L'action doit être 'add' ou 'delete'", null, null, true);
        }
        if (droitIds == null || droitIds.isEmpty()) {
            return new ManageRoleDroitsResult("droitIds est requis et ne peut pas être vide", null, null, true);
        }

        List<Integer> processedDroitIds = new ArrayList<>();
        List<String>  errors            = new ArrayList<>();

        for (Integer droitId : droitIds) {
            Droit droit = droitRepository.findById(droitId).orElse(null);
            if (droit == null) {
                errors.add("Droit ID " + droitId + " non trouvé");
                continue;
            }
            if ("add".equals(action)) {
                if (!roleRepository.existsRoleDroit(roleId, droitId)) {
                    roleRepository.addDroitToRole(roleId, droitId);
                    processedDroitIds.add(droitId);
                } else {
                    errors.add("Droit ID " + droitId + " est déjà assigné au rôle");
                }
            } else {
                if (roleRepository.existsRoleDroit(roleId, droitId)) {
                    roleRepository.removeDroitFromRole(roleId, droitId);
                    processedDroitIds.add(droitId);
                } else {
                    errors.add("Droit ID " + droitId + " n'est pas assigné au rôle");
                }
            }
        }

        if (processedDroitIds.isEmpty() && !errors.isEmpty()) {
            return new ManageRoleDroitsResult("Aucun droit n'a pu être traité", processedDroitIds, errors, true);
        }

        String actionMessage = "add".equals(action) ? "assignés au" : "supprimés du";
        String message = processedDroitIds.size() + " droit(s) " + actionMessage + " rôle avec succès";
        return new ManageRoleDroitsResult(message, processedDroitIds, errors.isEmpty() ? null : errors, false);
    }

    // ==================== USER DROIT MANAGEMENT ====================

    @Transactional
    public String assignDroitToUser(Integer userId, Integer droitId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "Utilisateur non trouvé";

        Droit droit = droitRepository.findById(droitId).orElse(null);
        if (droit == null) return "Droit non trouvé";

        if (userDroitRepository.findByUserAndDroit(user, droit).isPresent()) {
            return "L'utilisateur a déjà ce droit";
        }

        UserDroit userDroit = new UserDroit();
        userDroit.setUser(user);
        userDroit.setDroit(droit);
        userDroit.setStatus(GlobalConstants.STATUT_ACTIF);
        userDroitRepository.save(userDroit);

        return null; // null means success
    }

    @Transactional
    public String removeDroitFromUser(Integer userId, Integer droitId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "Utilisateur non trouvé";

        Droit droit = droitRepository.findById(droitId).orElse(null);
        if (droit == null) return "Droit non trouvé";

        userDroitRepository.deleteByUserAndDroit(user, droit);
        return null; // null means success
    }

    @Transactional
    public Map<String, Object> getUserDroits(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        List<UserDroit> userDroits = userDroitRepository.findByUserAndStatus(user, "Y");

        return Map.of(
                "userId", userId,
                "login",  user.getLogin(),
                "droits", userDroits
        );
    }
}
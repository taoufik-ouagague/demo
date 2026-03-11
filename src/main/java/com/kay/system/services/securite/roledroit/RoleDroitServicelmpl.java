package com.kay.system.services.securite.roledroit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kay.system.entity.Droit;
import com.kay.system.entity.Role;
import com.kay.system.repository.DroitRepository;
import com.kay.system.repository.RoleRepository;

import java.util.*;

@Service
public class RoleDroitServicelmpl implements IRoleDroitService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DroitRepository droitRepository;

    public static class ManageRoleDroitsResult {
        public boolean hasError;
        public String message;
        public List<Integer> processedDroitIds;
        public List<String> errors;

        public ManageRoleDroitsResult(boolean hasError,
                String message,
                List<Integer> processedDroitIds,
                List<String> errors) {
            this.hasError = hasError;
            this.message = message;
            this.processedDroitIds = processedDroitIds;
            this.errors = errors;
        }
    }

    // ------------------------------------------------------------------ //
    // getRoleDroits //
    // ------------------------------------------------------------------ //
    @Override
    public Map<String, Object> getRoleDroits(Integer roleId) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) {
            return null;
        }

        Role role = roleOpt.get();
        List<Droit> allDroits = droitRepository.findAll();
        Set<Droit> assignedSet = role.getDroits(); // assuming Set<Droit> on Role

        List<Map<String, Object>> assigned = new ArrayList<>();
        List<Map<String, Object>> available = new ArrayList<>();

        for (Droit d : allDroits) {
            Map<String, Object> entry = Map.of(
                    "id", d.getId(),
                    "code", d.getCode(),
                    "description", d.getDescription() != null ? d.getDescription() : "");
            if (assignedSet.contains(d)) {
                assigned.add(entry);
            } else {
                available.add(entry);
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("roleId", roleId);
        result.put("roleCode", role.getCode());
        result.put("assigned", assigned);
        result.put("available", available);
        return result;
    }

    // manageRoleDroits //

    @Override
    @Transactional
    public ManageRoleDroitsResult manageRoleDroits(Integer roleId,
            String action,
            List<Integer> droitIds) {
        // validate action //
        if (action == null || (!action.equals("add") && !action.equals("delete"))) {
            return new ManageRoleDroitsResult(
                    true,
                    "Action invalide. Utilisez 'add' ou 'delete'.",
                    null,
                    null);
        }

        // validate droitIds //
        if (droitIds == null || droitIds.isEmpty()) {
            return new ManageRoleDroitsResult(
                    true,
                    "La liste des droitIds est vide ou absente.",
                    null,
                    null);
        }
        // validate role //
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) {
            return new ManageRoleDroitsResult(
                    true,
                    "Rôle introuvable : " + roleId,
                    null,
                    null);
        }

        Role role = roleOpt.get();
        List<Integer> processed = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (Integer droitId : droitIds) {
            Optional<Droit> droitOpt = droitRepository.findById(droitId);
            if (droitOpt.isEmpty()) {
                errors.add("Droit introuvable : " + droitId);
                continue;
            }

            Droit droit = droitOpt.get();

            switch (action) {
                case "add" -> {
                    if (role.getDroits().contains(droit)) {
                        errors.add("Droit déjà assigné au rôle : " + droitId);
                    } else {
                        role.getDroits().add(droit);
                        processed.add(droitId);
                    }
                }
                case "delete" -> {
                    if (!role.getDroits().contains(droit)) {
                        errors.add("Droit non assigné au rôle : " + droitId);
                    } else {
                        role.getDroits().remove(droit);
                        processed.add(droitId);
                    }
                }
            }
        }

        if (!processed.isEmpty()) {
            roleRepository.save(role);
        }

        boolean hasError = !errors.isEmpty();
        String message = processed.isEmpty()
                ? "Aucun droit traité."
                : processed.size() + " droit(s) " + (action.equals("add") ? "ajouté(s)" : "supprimé(s)")
                        + " avec succès.";

        return new ManageRoleDroitsResult(hasError, message, processed, errors);
    }
}
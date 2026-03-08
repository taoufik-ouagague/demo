package com.example.demo.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.constants.GlobalConstants;
import com.example.demo.controller.securite.role.RoleBody;
import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Page<Role> listRoles(RoleBody filters, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return roleRepository.searchRoles(
                filters.getLibelle(),
                filters.getCode(),
                filters.getStatus(),
                pageable
        );
    }

    public Role getRoleById(Integer roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }

    public String createRole(Map<String, String> request) {
        String code        = request.get("code");
        String libelle     = request.get("libelle");
        String description = request.get("description");

        if (code == null || libelle == null) {
            return "Code et libelle sont requis";
        }
        if (roleRepository.findByCode(code).isPresent()) {
            return "Le code existe déjà";
        }

        Role role = new Role();
        role.setCode(code);
        role.setLibelle(libelle);
        role.setDescription(description);
        role.setStatus(GlobalConstants.STATUT_ACTIF);
        roleRepository.save(role);

        return null; // null means success
    }

    public String updateRole(Integer roleId, Map<String, String> request) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            return "Rôle non trouvé";
        }

        String code = request.get("code");
        if (code != null && !code.isEmpty()) {
            if (!code.equals(role.getCode()) && roleRepository.findByCode(code).isPresent()) {
                return "Le code existe déjà";
            }
            role.setCode(code);
        }

        String libelle = request.get("libelle");
        if (libelle != null && !libelle.isEmpty()) {
            role.setLibelle(libelle);
        }

        String description = request.get("description");
        if (description != null) {
            role.setDescription(description);
        }

        roleRepository.save(role);
        return null; // null means success
    }

    public boolean deleteRole(Integer roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) return false;
        role.setStatus(GlobalConstants.STATUT_DELETE);
        roleRepository.save(role);
        return true;
    }
}
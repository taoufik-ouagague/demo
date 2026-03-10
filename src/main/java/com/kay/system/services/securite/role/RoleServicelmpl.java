package com.kay.system.services.securite.role;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kay.system.constants.GlobalConstants;
import com.kay.system.controller.securite.role.RoleBody;
import com.kay.system.entity.Role;
import com.kay.system.repository.RoleRepository;

@Service
public class RoleServicelmpl implements IRoleService   {

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public Page<Role> listRoles(RoleBody filters, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return roleRepository.searchRoles(
                filters.getLibelle(),
                filters.getCode(),
                filters.getStatus(),
                pageable
        );
    }


    @Transactional
    public Role getRoleById(Integer roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }


    @Transactional
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

    
    @Transactional
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

    @Transactional
    public boolean deleteRole(Integer roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) return false;
        role.setStatus(GlobalConstants.STATUT_DELETE);
        roleRepository.save(role);
        return true;
    }
}
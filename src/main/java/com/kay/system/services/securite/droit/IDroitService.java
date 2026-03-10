package com.kay.system.services.securite.droit;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.kay.system.controller.securite.droit.DroitBody;
import com.kay.system.entity.Droit;

public interface IDroitService {

    // ==================== DROIT CRUD ====================

    Page<Droit> listDroits(DroitBody filters, int page, int size);

    Droit getDroitById(Integer droitId);

    String createDroit(Map<String, String> request);

    String updateDroit(Integer droitId, Map<String, String> request);

    boolean deleteDroit(Integer droitId);

    // ==================== ROLE DROIT MANAGEMENT ====================

    Map<String, Object> getRoleDroits(Integer roleId);

    DroitServicelmpl.ManageRoleDroitsResult manageRoleDroits(Integer roleId, String action, List<Integer> droitIds);

    // ==================== USER DROIT MANAGEMENT ====================

    String assignDroitToUser(Integer userId, Integer droitId);

    String removeDroitFromUser(Integer userId, Integer droitId);

    Map<String, Object> getUserDroits(Integer userId);
}
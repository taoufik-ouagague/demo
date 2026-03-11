package com.kay.system.services.securite.roledroit;

import java.util.List;
import java.util.Map;

public interface IRoleDroitService {


    Map<String, Object> getRoleDroits(Integer roleId);

    RoleDroitServicelmpl.ManageRoleDroitsResult manageRoleDroits(
            Integer roleId,
            String action,
            List<Integer> droitIds
    );
}
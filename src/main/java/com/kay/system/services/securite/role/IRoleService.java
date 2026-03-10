package com.kay.system.services.securite.role;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.kay.system.controller.securite.role.RoleBody;
import com.kay.system.entity.Role;

public interface IRoleService {

    Page<Role> listRoles(RoleBody filters, int page, int size);

    Role getRoleById(Integer roleId);

    String createRole(Map<String, String> request);

    String updateRole(Integer roleId, Map<String, String> request);

    boolean deleteRole(Integer roleId);
}
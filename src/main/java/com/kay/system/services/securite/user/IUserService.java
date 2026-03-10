package com.kay.system.services.securite.user;
import org.springframework.data.domain.Page;

import com.kay.system.controller.securite.user.UserBody;
import com.kay.system.entity.User;

public interface IUserService {

    Page<User> listUsers(UserBody filters, int page, int size);

    User getUserById(Integer userId);

    String createUser(UserBody request);

    String updateUser(Integer userId, String email, String libelle);

    boolean deleteUser(Integer userId);

    Boolean setUserStatus(Integer userId, boolean active);

    boolean resetPassword(Integer userId);


}
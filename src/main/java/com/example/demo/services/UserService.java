package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.constants.GlobalConstants;
import com.example.demo.controller.securite.user.UserBody;
import com.example.demo.entity.Role;
import com.example.demo.entity.RoleDroit;
import com.example.demo.entity.User;
import com.example.demo.entity.UserDroit;
import com.example.demo.repository.RoleDroitRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserDroitRepository;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleDroitRepository roleDroitRepository;

    @Autowired
    private UserDroitRepository userDroitRepository;

    public Page<User> listUsers(UserBody filters, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.searchUsers(
                filters.getLibelle(),
                filters.getLogin(),
                filters.getStatus(),
                pageable
        );
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public String createUser(UserBody request) {
        String login   = request.getLogin();
        String email   = request.getEmail();
        String libelle = request.getLibelle();
        Integer idRole = request.getIdRole();

        if (login == null || email == null) {
            return "Login et email sont obligatoires";
        }
        if (userRepository.existsByLogin(login)) {
            return "Le login existe déjà";
        }

        Role role = roleRepository.findById(idRole).orElse(null);
        if (role == null) {
            return "Rôle non trouvé";
        }

        User user = new User();
        user.setLogin(login);
        user.setEmail(email);
        user.setLibelle(libelle);
        user.setStatus(GlobalConstants.STATUT_ACTIF);
        user.setPwd(passwordEncoder.encode(login.toLowerCase()));
        user.setRole(role);

        User savedUser = userRepository.saveAndFlush(user);

        List<RoleDroit> droits = roleDroitRepository.getRolesDroits(role.getId(), GlobalConstants.STATUT_ACTIF);
        for (RoleDroit roleDroit : droits) {
            UserDroit userDroit = new UserDroit();
            userDroit.setUser(savedUser);
            userDroit.setDroit(roleDroit.getDroit());
            userDroit.setRole(role);
            userDroit.setStatus(GlobalConstants.STATUT_ACTIF);
            userDroitRepository.save(userDroit);
        }

        return null; // null means success
    }

    public String updateUser(Integer userId, String email, String libelle) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "Utilisateur non trouvé";
        }
        if (email != null && !email.isEmpty()) {
            if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
                return "L'email existe déjà";
            }
            user.setEmail(email);
        }
        if (libelle != null) {
            user.setLibelle(libelle);
        }
        userRepository.save(user);
        return null; // null means success
    }

    public boolean deleteUser(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;
        user.setStatus(GlobalConstants.STATUT_DELETE);
        userRepository.save(user);
        return true;
    }

    public Boolean setUserStatus(Integer userId, boolean active) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;
        user.setStatus(active ? GlobalConstants.STATUT_ACTIF : GlobalConstants.STATUT_INACTIF);
        userRepository.save(user);
        return active;
    }

    public boolean resetPassword(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;
        user.setPwd(passwordEncoder.encode(user.getLogin().toLowerCase()));
        userRepository.save(user);
        return true;
    }
}
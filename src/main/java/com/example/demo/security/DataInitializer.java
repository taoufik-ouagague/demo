package com.example.demo.security;

import com.example.demo.entity.*;
import com.example.demo.repository.DroitRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserDroitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DroitRepository droitRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDroitRepository userDroitRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeDroits();
        assignDroitsToRoles();
        initializeUsers();
    }

    private void initializeRoles() {
        // ROLE_ADMIN
        Optional<Role> adminRoleOpt = roleRepository.findByCode("ROLE_ADMIN");
        Role adminRole = adminRoleOpt.orElseGet(Role::new);
        adminRole.setCode("ROLE_ADMIN");
        adminRole.setLibelle("Administrateur");
        adminRole.setDescription("Accès complet au système avec toutes les permissions");
        if (adminRoleOpt.isEmpty()) {
            adminRole.setName(ERole.ROLE_ADMIN);
        }
        roleRepository.save(adminRole);
        System.out.println("✓ ROLE_ADMIN updated");

        // ROLE_NORMAL_USER
        Optional<Role> userRoleOpt = roleRepository.findByCode("ROLE_NORMAL_USER");
        Role userRole = userRoleOpt.orElseGet(Role::new);
        userRole.setCode("ROLE_NORMAL_USER");
        userRole.setLibelle("Utilisateur Normal");
        userRole.setDescription("Utilisateur régulier avec des permissions limitées");
        if (userRoleOpt.isEmpty()) {
            userRole.setName(ERole.ROLE_USER);
        }
        roleRepository.save(userRole);
        System.out.println("✓ ROLE_NORMAL_USER updated");

        // ROLE_CLIENT
        Optional<Role> clientRoleOpt = roleRepository.findByCode("ROLE_CLIENT");
        Role clientRole = clientRoleOpt.orElseGet(Role::new);
        clientRole.setCode("ROLE_CLIENT");
        clientRole.setLibelle("Client");
        clientRole.setDescription("Client avec des permissions minimales");
        if (clientRoleOpt.isEmpty()) {
            clientRole.setName(ERole.ROLE_USER);
        }
        roleRepository.save(clientRole);
        System.out.println("✓ ROLE_CLIENT updated");
    }

    private void initializeDroits() {
        initializeDroit("USER_ENABLE_DISABLE", "Activer ou désactiver le compte utilisateur", "Permettre à l'administrateur d'activer/désactiver les comptes utilisateurs");
        initializeDroit("USER_PASSWORD_RESET", "Réinitialiser le mot de passe utilisateur", "Permettre à l'administrateur de réinitialiser les mots de passe utilisateur");
        initializeDroit("USER_ASSIGN_ROLE", "Assigner un rôle à l'utilisateur", "Permettre à l'administrateur d'assigner des rôles aux utilisateurs");
        initializeDroit("USER_REMOVE_ROLE", "Supprimer le rôle de l'utilisateur", "Permettre à l'administrateur de supprimer des rôles des utilisateurs");
        initializeDroit("USER_ASSIGN_DROIT", "Assigner une permission à l'utilisateur", "Permettre à l'administrateur d'assigner des permissions aux utilisateurs");
        initializeDroit("USER_REMOVE_DROIT", "Supprimer la permission de l'utilisateur", "Permettre à l'administrateur de supprimer des permissions des utilisateurs");
        initializeDroit("USER_CREATE", "Créer un utilisateur", "Permettre à l'administrateur de créer de nouveaux utilisateurs");
        initializeDroit("USER_READ", "Lire les informations de l'utilisateur", "Permettre à l'utilisateur de voir les informations de l'utilisateur");
        initializeDroit("USER_UPDATE", "Mettre à jour les informations de l'utilisateur", "Permettre à l'administrateur de mettre à jour les informations de l'utilisateur");
        initializeDroit("USER_DELETE", "Supprimer l'utilisateur", "Permettre à l'administrateur de supprimer les utilisateurs");
        
        initializeDroit("ROLE_CREATE", "Créer un rôle", "Permettre à l'administrateur de créer de nouveaux rôles");
        initializeDroit("ROLE_READ", "Lire les informations du rôle", "Permettre de voir les informations du rôle");
        initializeDroit("ROLE_UPDATE", "Mettre à jour le rôle", "Permettre à l'administrateur de mettre à jour le rôle");
        initializeDroit("ROLE_DELETE", "Supprimer le rôle", "Permettre à l'administrateur de supprimer le rôle");
        initializeDroit("ROLE_ASSIGN_DROIT", "Assigner un droit au rôle", "Permettre à l'administrateur d'assigner des permissions aux rôles");
        
        initializeDroit("DROIT_CREATE", "Créer une permission", "Permettre à l'administrateur de créer de nouvelles permissions");
        initializeDroit("DROIT_READ", "Lire la permission", "Permettre de voir les permissions");
        initializeDroit("DROIT_UPDATE", "Mettre à jour la permission", "Permettre à l'administrateur de mettre à jour la permission");
        initializeDroit("DROIT_DELETE", "Supprimer la permission", "Permettre à l'administrateur de supprimer la permission");
        
        initializeDroit("ADMIN_ACCESS", "Accès administrateur", "Permettre un accès complet au panneau d'administration");
        initializeDroit("VIEW_DASHBOARD", "Afficher le tableau de bord", "Permettre à l'utilisateur d'afficher le tableau de bord");
        initializeDroit("VIEW_REPORTS", "Afficher les rapports", "Permettre à l'utilisateur d'afficher les rapports");
        initializeDroit("CONTENT_MODERATION", "Modération du contenu", "Permettre à l'utilisateur de modérer le contenu");
    }

    private void initializeDroit(String code, String libelle, String description) {
        Optional<Droit> existingDroit = droitRepository.findByCode(code);
        Droit droit = existingDroit.orElseGet(Droit::new);
        droit.setCode(code);
        droit.setLibelle(libelle);
        droit.setDescription(description);
        droitRepository.save(droit);
        if (existingDroit.isEmpty()) {
            System.out.println("✓ Droit " + code + " created");
        } else {
            System.out.println("✓ Droit " + code + " updated");
        }
    }

    private void assignDroitsToRoles() {
        // Assign droits to ROLE_ADMIN using direct SQL to avoid collection loading
        Optional<Role> adminRoleOpt = roleRepository.findByCode("ROLE_ADMIN");
        if (adminRoleOpt.isPresent()) {
            Role adminRole = adminRoleOpt.get();
            int droitsAdded = 0;
            
            String[] adminDroitCodes = {
                "USER_ENABLE_DISABLE", "USER_PASSWORD_RESET", "USER_ASSIGN_ROLE",
                "USER_REMOVE_ROLE", "USER_ASSIGN_DROIT", "USER_REMOVE_DROIT",
                "USER_CREATE", "USER_READ", "USER_UPDATE", "USER_DELETE",
                "ROLE_CREATE", "ROLE_READ", "ROLE_UPDATE", "ROLE_DELETE",
                "ROLE_ASSIGN_DROIT",
                "DROIT_CREATE", "DROIT_READ", "DROIT_UPDATE", "DROIT_DELETE",
                "ADMIN_ACCESS", "VIEW_DASHBOARD", "VIEW_REPORTS"
            };
            
            for (String code : adminDroitCodes) {
                Optional<Droit> droit = droitRepository.findByCode(code);
                if (droit.isPresent()) {
                    int inserted = roleRepository.addDroitToRole(adminRole.getId(), droit.get().getId());
                    if (inserted > 0) {
                        droitsAdded++;
                    }
                }
            }
            System.out.println("✓ Assigned " + droitsAdded + " new droits to ROLE_ADMIN");
        }

        // Assign droits to ROLE_NORMAL_USER using direct SQL to avoid collection loading
        Optional<Role> userRoleOpt = roleRepository.findByCode("ROLE_NORMAL_USER");
        if (userRoleOpt.isPresent()) {
            Role userRole = userRoleOpt.get();
            int droitsAdded = 0;
            
            String[] userDroitCodes = {
                "USER_READ", "ROLE_READ", "DROIT_READ",
                "VIEW_DASHBOARD", "VIEW_REPORTS", "CONTENT_MODERATION"
            };
            
            for (String code : userDroitCodes) {
                Optional<Droit> droit = droitRepository.findByCode(code);
                if (droit.isPresent()) {
                    int inserted = roleRepository.addDroitToRole(userRole.getId(), droit.get().getId());
                    if (inserted > 0) {
                        droitsAdded++;
                    }
                }
            }
            System.out.println("✓ Assigned " + droitsAdded + " new droits to ROLE_NORMAL_USER");
        }

        // Assign droits to ROLE_CLIENT using direct SQL to avoid collection loading
        Optional<Role> clientRoleOpt = roleRepository.findByCode("ROLE_CLIENT");
        if (clientRoleOpt.isPresent()) {
            Role clientRole = clientRoleOpt.get();
            int droitsAdded = 0;
            
            String[] clientDroitCodes = {
                "USER_READ", "VIEW_DASHBOARD"
            };
            
            for (String code : clientDroitCodes) {
                Optional<Droit> droit = droitRepository.findByCode(code);
                if (droit.isPresent()) {
                    int inserted = roleRepository.addDroitToRole(clientRole.getId(), droit.get().getId());
                    if (inserted > 0) {
                        droitsAdded++;
                    }
                }
            }
            System.out.println("✓ Assigned " + droitsAdded + " new droits to ROLE_CLIENT");
        }
    }

    private void initializeUsers() {
        // Create admin user
        Optional<User> existingAdmin = userRepository.findByLogin("admin");
        if (existingAdmin.isEmpty()) {
            Optional<Role> adminRoleOpt = roleRepository.findByCode("ROLE_ADMIN");
            if (adminRoleOpt.isPresent()) {
                Role adminRole = adminRoleOpt.get();
                
                User adminUser = new User();
                adminUser.setLogin("admin");
                adminUser.setEmail("admin@example.com");
                adminUser.setPwd(passwordEncoder.encode("Admin@123"));
                adminUser.setStatus(true);
                adminUser.setDateCreation(LocalDateTime.now());
                adminUser.setIdRole(adminRole.getId());
                
                userRepository.save(adminUser);
                assignRoleDroitsToUser(adminUser, adminRole);
                System.out.println("✓ Admin user created: admin / Admin@123");
            }
        }
        
        // Create normal user (USER)
        Optional<User> existingNormalUser = userRepository.findByLogin("USER");
        if (existingNormalUser.isEmpty()) {
            Optional<Role> userRoleOpt = roleRepository.findByCode("ROLE_NORMAL_USER");
            if (userRoleOpt.isPresent()) {
                Role userRole = userRoleOpt.get();
                
                User normalUser = new User();
                normalUser.setLogin("USER");
                normalUser.setEmail("user@example.com");
                normalUser.setPwd(passwordEncoder.encode("Admin@123"));
                normalUser.setStatus(true);
                normalUser.setDateCreation(LocalDateTime.now());
                normalUser.setIdRole(userRole.getId());
                
                userRepository.save(normalUser);
                assignRoleDroitsToUser(normalUser, userRole);
                System.out.println("✓ Normal user created: USER / Admin@123");
            }
        }
    }
    
    private void assignRoleDroitsToUser(User user, Role role) {
        for (Droit droit : role.getDroits()) {
            UserDroit userDroit = new UserDroit(user, droit);
            userDroitRepository.save(userDroit);
        }
    }
}

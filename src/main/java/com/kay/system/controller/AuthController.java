package com.kay.system.controller;

import com.kay.system.entity.User;
import com.kay.system.repository.RoleRepository;
import com.kay.system.repository.UserRepository;
import com.kay.system.security.JwtUtils;
import com.kay.system.security.UserDetailsImpl;
import com.payload.LoginRequest;
import com.payload.RegisterRequest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getLogin(),
                            loginRequest.getPwd())
            );

            // Check if user account is enabled
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            var user = userRepository.findByLogin(userDetails.getUsername());
            if (user.isPresent() && (user.get().getStatus() == null || !"1".equals(user.get().getStatus()))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(java.util.Map.of(
                                "message", "Identifiants de connexion invalides",
                                "error", "Le compte utilisateur est désactivé"
                        ));
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(loginRequest.getLogin());
            
//System.out.println(userDetails.getAuthorities().size() + " authorities found for user " + userDetails.getUsername());
            return ResponseEntity.ok(new LoginResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList()));
        } catch (Exception e) {
            System.err.println("Login error: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of(
                            "message", "Identifiants de connexion invalides",
                            "error", e.getMessage() != null ? e.getMessage() : e.getClass().getName()
                    ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        if (userRepository.existsByLogin(signUpRequest.getLogin())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Erreur : Le login est déjà utilisé !"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Erreur : L'email est déjà utilisé !"));
        }

        // Create new user's account
        User user = new User();
        user.setLogin(signUpRequest.getLogin());
        user.setEmail(signUpRequest.getEmail());
        user.setPwd(encoder.encode(signUpRequest.getPwd()));
        user.setLibelle(signUpRequest.getLogin());  // Set display name from login
        user.setStatus("1");  // Enable user by default
        user.setDateCreation(new java.util.Date());  // Set creation timestamp
        
        // Assign default role - try USER first, then fallback to first available role
        var defaultRole = roleRepository.findByCode("USER");
        if (defaultRole.isEmpty()) {
            defaultRole = roleRepository.findById(1);  // Fallback to role with ID 1
        }
        if (defaultRole.isPresent()) {
            user.setRole(defaultRole.get());
        }

        User savedUser = userRepository.save(user);

        // Generate JWT token for the new user
        String jwt = jwtUtils.generateJwtToken(savedUser.getLogin());

        return ResponseEntity.ok(new LoginResponse(jwt,
                savedUser.getId(),
                savedUser.getLogin(),
                savedUser.getEmail(),
                List.of()));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return ResponseEntity.ok(userDetails);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    public static class LoginResponse {
        public String token;
        public String type = "Bearer";
        public Integer id;
        public String username;
        public String email;
        public java.util.List<String> roles;

        public LoginResponse(String accessToken, Integer id, String username, String email,
                           java.util.List<String> roles) {
            this.token = accessToken;
            this.id = id;
            this.username = username;
            this.email = email;
            this.roles = roles;
        }
    }

    public static class MessageResponse {
        public String message;

        public MessageResponse(String message) {
            this.message = message;
        }
    }
}

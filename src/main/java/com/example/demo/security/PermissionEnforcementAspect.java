package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.repository.UserDroitRepository;
import com.example.demo.repository.UserRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Aspect to enforce permission-based access control using @RequirePermission annotation
 */
@Aspect
@Component
public class PermissionEnforcementAspect {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDroitRepository userDroitRepository;

    /**
     * Pointcut for methods annotated with @RequirePermission
     */
    @Pointcut("@annotation(requirePermission)")
    public void requirePermissionMethods(RequirePermission requirePermission) {
    }

    /**
     * Before advice to check if user has the required permission
     */
    @Before("requirePermissionMethods(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        String requiredPermissionCode = requirePermission.value();

        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("L'utilisateur n'est pas authentifié");
        }

        String username = authentication.getName();
        
        // Find the user in database
        Optional<User> userOpt = userRepository.findByLogin(username);
        if (userOpt.isEmpty()) {
            throw new AccessDeniedException("Utilisateur non trouvé");
        }

        User user = userOpt.get();

        // Check if user has the required permission/droit
        boolean hasPermission = userDroitRepository.existsByUserAndDroitCode(user, requiredPermissionCode);

        if (!hasPermission) {
            throw new AccessDeniedException("L'utilisateur n'a pas la permission requise: " + requiredPermissionCode);
        }
    }
}

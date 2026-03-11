package com.kay.system.services.securite.userdroit;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kay.system.entity.Droit;
import com.kay.system.entity.User;
import com.kay.system.entity.UserDroit;
import com.kay.system.repository.DroitRepository;
import com.kay.system.repository.UserDroitRepository;
import com.kay.system.repository.UserRepository;



@Service
public class UserDroitServiceImpl implements IUserDroitService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DroitRepository droitRepository;

    @Autowired
    private UserDroitRepository userDroitRepository;

    
    // ASSIGN a droit to a user
    @Override
    public String assignDroitToUser(Integer userId, Integer droitId) {

        // 1. Check user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "Utilisateur introuvable avec l'ID : " + userId;
        }

        // 2. Check droit exists
        Droit droit = droitRepository.findById(droitId).orElse(null);
        if (droit == null) {
            return "Droit introuvable avec l'ID : " + droitId;
        }

        // 3. Check if already assigned
        boolean alreadyAssigned = userDroitRepository
                .existsByUserIdAndDroitId(userId, droitId);
        if (alreadyAssigned) {
            return "Ce droit est déjà assigné à cet utilisateur.";
        }

        // 4. Create and save the assignment
        UserDroit userDroit = new UserDroit();
        userDroit.setUser(user);
        userDroit.setDroit(droit);
        userDroit.setRole(user.getRole());  // Set the user's role as the context for this droit
        userDroitRepository.save(userDroit);

        return null; // success
    }

    
    // REMOVE a droit from a user
    @Override
    public String removeDroitFromUser(Integer userId, Integer droitId) {

        // 1. Check user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "Utilisateur introuvable avec l'ID : " + userId;
        }

        // 2. Check droit exists
        Droit droit = droitRepository.findById(droitId).orElse(null);
        if (droit == null) {
            return "Droit introuvable avec l'ID : " + droitId;
        }

        // 3. Check if the assignment actually exists
        UserDroit userDroit = userDroitRepository
                .findByUserIdAndDroitId(userId, droitId);
        if (userDroit == null) {
            return "Ce droit n'est pas assigné à cet utilisateur.";
        }

        // 4. Delete the assignment
        userDroitRepository.delete(userDroit);

        return null; // success
    }

    // GET ALL droits (assigned + unassigned) for a user
    @Override
    public Map<String, Object> getAllDroitsForUser(Integer userId) {

        // 1. Check user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        // 2. Fetch all droits in the system
        List<Droit> allDroits = droitRepository.findAll();

        // 3. Fetch droits already assigned to this user
        List<Droit> assignedDroits = userDroitRepository
                .findDroitsByUserId(userId);

        // 4. Build the unassigned list by excluding assigned ones
        List<Integer> assignedIds = assignedDroits.stream()
                .map(Droit::getId)
                .collect(Collectors.toList());

        List<Droit> unassignedDroits = allDroits.stream()
                .filter(d -> !assignedIds.contains(d.getId()))
                .collect(Collectors.toList());

        // 5. Build and return the response map
        Map<String, Object> result = new HashMap<>();
        result.put("userId",     userId);
        result.put("userName",   user.getUsername()); // or user.getNom() depending on your model
        result.put("assigned",   assignedDroits);
        result.put("unassigned", unassignedDroits);

        return result;
    }
}
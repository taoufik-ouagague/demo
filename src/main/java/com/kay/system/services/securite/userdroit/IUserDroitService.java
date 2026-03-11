package com.kay.system.services.securite.userdroit;

import java.util.Map;

public interface IUserDroitService {


    String assignDroitToUser(Integer userId, Integer droitId);


    String removeDroitFromUser(Integer userId, Integer droitId);


    Map<String, Object> getAllDroitsForUser(Integer userId);
}
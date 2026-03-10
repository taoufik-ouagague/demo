package com.kay.system.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kay.system.repository.UserDroitRepository;
import com.kay.system.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDroitRepository userDroitRepository;
   

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        var user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + username));

        return UserDetailsImpl.build(user,  userDroitRepository);
    }
}

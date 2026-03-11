package com.kay.system.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kay.system.constants.GlobalConstants;
import com.kay.system.entity.User;
import com.kay.system.entity.UserDroit;
import com.kay.system.repository.UserDroitRepository;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;




public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Autowired
    UserDroitRepository userDroitRepository;

    private Integer id;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Integer id, String username, String email, String password,
            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user, UserDroitRepository userDroitRepository) {
        // Load authorities from user's role and droits
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Add user's role as authority (using libelle, not code)
        if (user.getRole() != null && user.getRole().getLibelle() != null) {
            authorities.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return user.getRole().getLibelle();
                }
            });
        }

        // Add individual droits as authorities
        List<UserDroit> droits = userDroitRepository.findByUserAndStatus(user, GlobalConstants.STATUT_ACTIF);
    
        for (UserDroit userDroit : droits) {
                authorities.add(new GrantedAuthority() {
                    @Override
                    public String getAuthority() {
                        return userDroit.getDroit().getCode();
                    }
                });
            }
        return new UserDetailsImpl(
                user.getId(),
                user.getLogin(),
                user.getEmail(),
                user.getPwd(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}

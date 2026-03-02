package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user_droit")
public class UserDroit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "droit_id", nullable = false)
    private Droit droit;
    
    @Column(name = "date_attribution")
    private LocalDateTime dateAttribution;
    
    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;
    
    @Column(name = "status")
    private Boolean status = true;
    
    public UserDroit() {
        this.dateAttribution = LocalDateTime.now();
    }
    
    public UserDroit(User user, Droit droit) {
        this.user = user;
        this.droit = droit;
        this.dateAttribution = LocalDateTime.now();
        this.status = true;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Droit getDroit() {
        return droit;
    }
    
    public void setDroit(Droit droit) {
        this.droit = droit;
    }
    
    public LocalDateTime getDateAttribution() {
        return dateAttribution;
    }
    
    public void setDateAttribution(LocalDateTime dateAttribution) {
        this.dateAttribution = dateAttribution;
    }
    
    public LocalDateTime getDateExpiration() {
        return dateExpiration;
    }
    
    public void setDateExpiration(LocalDateTime dateExpiration) {
        this.dateExpiration = dateExpiration;
    }
    
    public Boolean getStatus() {
        return status;
    }
    
    public void setStatus(Boolean status) {
        this.status = status;
    }
}

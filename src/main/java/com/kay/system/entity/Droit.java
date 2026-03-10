package com.kay.system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "droit")
public class Droit extends EntityClass {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    
    
    @NotBlank
    private String libelle; // e.g., "Enable or disable user"
    
    private String description;
    
    public Droit() {}
    
    public Droit(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }
    
    public Droit(String code, String libelle, String description) {
        this.code = code;
        this.libelle = libelle;
        this.description = description;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}

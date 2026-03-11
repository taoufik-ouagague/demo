package com.kay.system.entity;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "droit")
public class Droit extends EntityClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String code; 

    @NotBlank
    private String libelle; 

    private String description;

    public static class DroitIdsRequest {
        private List<Integer> droitIds;

        public DroitIdsRequest() {
        }

        public DroitIdsRequest(List<Integer> droitIds) {
            this.droitIds = droitIds;
        }

        public List<Integer> getDroitIds() {
            return droitIds;
        }

        public void setDroitIds(List<Integer> droitIds) {
            this.droitIds = droitIds;
        }
    }

    public Droit() {
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
    } }

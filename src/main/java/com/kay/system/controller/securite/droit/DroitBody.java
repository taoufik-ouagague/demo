package com.kay.system.controller.securite.droit;

public class DroitBody {
    private String libelle;
    private String code;
    private String status;

    public String getLibelle() {
        return libelle;
    }

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

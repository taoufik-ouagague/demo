package com.kay.system.controller.securite.user;

public class UserBody {
    private String libelle;
    private String login;
    private String status;
    private Integer idRole;
    private String email;

     public String getEmail() {
        return email;
    }

    public Integer getIdRole() {
        return idRole;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

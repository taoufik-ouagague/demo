package com.kay.system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Size;
import java.util.Date;

@MappedSuperclass
public class EntityClass {
    @Size(max = 20)
    @Column(name = "CODE", length = 20)
    protected String code;

    @Size(max = 3)
    @Column(name = "STATUS", length = 3)
    protected String status;

    @Column(name = "DATE_CREATION")
    protected Date dateCreation;
    @Column(name = "DATE_MODIFICATION")
    protected Date dateModification;

    @Column(name = "DATE_DESACTIVATION")
    protected Date dateDesactivation;

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status   ;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setDateModification(Date dateModification) {
        this.dateModification = dateModification;
    }

    public void setDateDesactivation(Date dateDesactivation) {
        this.dateDesactivation = dateDesactivation;
    }

    public Date getDateModification() {
        return dateModification;
    }

    public Date getDateDesactivation() {
        return dateDesactivation;
    }

}

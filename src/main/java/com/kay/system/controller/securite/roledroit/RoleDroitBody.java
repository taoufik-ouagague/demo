package com.kay.system.controller.securite.roledroit;

import java.util.List;

public class RoleDroitBody {
    private String action;
    private List<Integer> droitIds;

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public List<Integer> getDroitIds() { return droitIds; }
    public void setDroitIds(List<Integer> droitIds) { this.droitIds = droitIds; }
}
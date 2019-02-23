package com.exloki.pexconverter.model;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    private String uniqueId;
    private String lastKnownName;

    private List<String> groups;
    private List<String> permissions;

    private String prefix;
    private String suffix;

    public PlayerData(String uniqueId, String lastKnownName) {
        this.uniqueId = uniqueId;
        this.lastKnownName = lastKnownName;

        this.groups = new ArrayList<>();
        this.permissions = new ArrayList<>();
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getLastKnownName() {
        return lastKnownName;
    }

    public List<String> getGroups() {
        return groups;
    }

    public String getPrimaryGroup() {
        return this.groups.size() > 0 ? groups.get(0) : "";
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public void addGroup(String group) {
        this.groups.add(group);
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}

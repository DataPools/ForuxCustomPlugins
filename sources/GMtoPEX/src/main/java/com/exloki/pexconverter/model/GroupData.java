package com.exloki.pexconverter.model;

import java.util.ArrayList;
import java.util.List;

public class GroupData {
    private String name;
    private boolean defaultGroup;

    private List<String> permissions;
    private List<String> inheritedGroups;

    private String prefix;
    private String suffix;

    public GroupData(String name, boolean defaultGroup) {
        this.name = name;
        this.defaultGroup = defaultGroup;

        this.permissions = new ArrayList<String>();
        this.inheritedGroups = new ArrayList<String>();
        this.prefix = "";
        this.suffix = "";
    }

    public String getName() {
        return name;
    }

    public boolean isDefaultGroup() {
        return defaultGroup;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public List<String> getInheritedGroups() {
        return inheritedGroups;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setDefaultGroup(boolean defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    public void setInheritedGroups(List<String> inheritedGroups) {
        this.inheritedGroups = inheritedGroups;
    }

    public void addInheritedGroup(String group) {
        this.inheritedGroups.add(group);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}

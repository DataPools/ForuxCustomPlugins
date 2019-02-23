package com.exloki.pexconverter.model;

import java.util.ArrayList;
import java.util.List;

public abstract class PermissionsPluginData {
    private String permissionsPlugin;

    protected List<GroupData> groups;
    protected List<PlayerData> players;

    public PermissionsPluginData(String permissionsPlugin) {
        this.permissionsPlugin = permissionsPlugin;
        this.groups = new ArrayList<>();
        this.players = new ArrayList<>();
    }

    public PermissionsPluginData(String permissionsPlugin, List<GroupData> groups, List<PlayerData> players) {
        this.permissionsPlugin = permissionsPlugin;
        this.groups = groups;
        this.players = players;
    }

    public String getPermissionsPlugin() {
        return permissionsPlugin;
    }

    public List<GroupData> getGroups() {
        return groups;
    }

    public void addGroup(GroupData groupData) {
        this.groups.add(groupData);
    }

    public List<PlayerData> getPlayers() {
        return players;
    }

    public void addPlayer(PlayerData playerData) {
        this.players.add(playerData);
    }

    public void setGroups(List<GroupData> groups) {
        this.groups = groups;
    }

    public void setPlayers(List<PlayerData> players) {
        this.players = players;
    }
}

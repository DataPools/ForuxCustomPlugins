package com.exloki.pexconverter.model;

import java.nio.file.Path;

public interface PermissionsPluginExporter {
    void exportGroups(Path pluginFolder) throws PermissionsPluginPortException;
    void exportPlayers(Path pluginFolder) throws PermissionsPluginPortException;
}
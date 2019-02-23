package com.exloki.pexconverter.model;

import java.nio.file.Path;

public interface PermissionsPluginImporter {
    void importGroups(Path pluginFolder) throws PermissionsPluginPortException;
    void importPlayers(Path pluginFolder) throws PermissionsPluginPortException;
}

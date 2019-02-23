package com.exloki.foruxd.hooks;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    public static VaultHook i;

    static {
        i = new VaultHook();
    }

    private static boolean hooked;
    public static Permission permissions;
    public static Economy economy;

    public void hook() {
        if (!isHooked() && canHook()) {
            RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
            if (permissionProvider != null) permissions = permissionProvider.getProvider();

            RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) economy = economyProvider.getProvider();

            hooked = true;
        }
    }

    public void unhook() {
        permissions = null;
        economy = null;
        hooked = false;
    }

    public boolean canHook() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Vault");
        return plugin != null && plugin.isEnabled();
    }

    public boolean isHooked() {
        return hooked;
    }

}

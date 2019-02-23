package com.exloki.balancedfly;

import com.exloki.balancedfly.core.LPlugin;
import com.exloki.balancedfly.core.persist.ReadOnlyResource;
import com.exloki.balancedfly.core.persist.YamlResourceFile;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;

@Getter
public class BalancedFlyMain extends LPlugin {

    @ReadOnlyResource
    @YamlResourceFile(filename = "config.yml")
    private Settings settings = null; // Assigned by ResourceFileManager

    private FlightManager flightManager;

	/*
     * Start / Stop
	 */

    public BalancedFlyMain() {
        super("[BalancedFly] ");
    }

    @Override
    public void onStart() {
        if(settings == null) {
            throw new IllegalStateException("settings cannot be null");
        }
        messagePrefix = settings.getPrefixMessage();

        flightManager = new FlightManager(this);

        registerListeners(pm);

        log(String.format("%s enabled (version %s) - Author: Exloki", this.getDescription().getName(), this.getDescription().getVersion()));
    }

    @Override
    public void onStop() {
        log(String.format("%s disabled (version %s) - Author: Exloki", this.getDescription().getName(), this.getDescription().getVersion()));
    }

	/*
	 * Listeners
	 */

    private void registerListeners(PluginManager pm) {
        pm.registerEvents(flightManager, this);
    }
}

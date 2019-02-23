package com.exloki.foruxmotd;

import com.exloki.foruxmotd.commands.CommandMOTD;
import com.exloki.foruxmotd.core.LPlugin;
import com.exloki.foruxmotd.core.persist.ReadOnlyResource;
import com.exloki.foruxmotd.core.persist.YamlResourceFile;
import com.exloki.foruxmotd.model.MotdManager;
import lombok.Getter;

@Getter
public class ForuxMOTD extends LPlugin {
    @ReadOnlyResource
    @YamlResourceFile(filename = "config.yml")
    private Settings settings = null; // Assigned by ResourceFileManager

    private MotdManager motdManager;

	/*
     * Start / Stop
	 */

    public ForuxMOTD() {
        super("[ForuxMOTD] ");
    }

    @Override
    public void onStart() {
        if(settings == null) {
            throw new IllegalStateException("settings cannot be null");
        }
        messagePrefix = settings.getPrefixMessage();

        motdManager = new MotdManager(this);

        registerCommand(new CommandMOTD());

        log(String.format("%s enabled (version %s) - Author: Exloki", this.getDescription().getName(), this.getDescription().getVersion()));
    }

    @Override
    public void onStop() {
        log(String.format("%s disabled (version %s) - Author: Exloki", this.getDescription().getName(), this.getDescription().getVersion()));
    }
}

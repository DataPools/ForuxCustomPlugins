package com.exloki.loretransfer;

import com.exloki.loretransfer.core.LPlugin;
import com.exloki.loretransfer.core.config.LokiConfig;

import java.io.File;


public class Settings {
    private final transient LokiConfig config;
    protected final transient LPlugin pl;

    public Settings(LPlugin pl) {
        this.pl = pl;
        config = new LokiConfig(new File(pl.getDataFolder(), "config.yml"));
        config.setTemplateName("/config.yml", LoreTransfer.class);
        reloadConfig();
    }

    public void reloadConfig() {
        config.load();
        debug = _isDebug();
        prefix_message = _getPrefixMessage();
    }

    private boolean debug = false;

    private boolean _isDebug() {
        return config.getBoolean("debug", false);
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    private String prefix_message = "";

    private String _getPrefixMessage() {
        return config.getString("prefix", "[PREFIX] ");
    }

    public String getPrefixMessage() {
        return prefix_message;
    }

    public void setPrefixMessage(final String message) {
        this.prefix_message = message;
    }
}
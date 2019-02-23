package com.exloki.foruxi;

import com.exloki.foruxi.core.LPlugin;
import com.exloki.foruxi.core.config.LokiConfig;

import java.io.File;


public class Settings {
    private final transient LokiConfig config;
    protected final transient LPlugin pl;

    public Settings(LPlugin pl) {
        this.pl = pl;
        config = new LokiConfig(new File(pl.getDataFolder(), "config.yml"));
        config.setTemplateName("/config.yml", ForuxInvisibility.class);
        reloadConfig();
    }

    public void reloadConfig() {
        config.load();
        debug = _isDebug();
        prefix_message = _getPrefixMessage();
        cooldownDurationSeconds = _getCooldownDurationInSeconds();
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
        return config.getString("prefix_message", "[PREFIX] ");
    }

    public String getPrefixMessage() {
        return prefix_message;
    }

    public void setPrefixMessage(final String message) {
        this.prefix_message = message;
    }

    private int cooldownDurationSeconds = 120;

    private int _getCooldownDurationInSeconds() {
        return config.getInt("oooldown_duration_in_seconds");
    }

    public int getCooldownDurationInSeconds() {
        return cooldownDurationSeconds;
    }
}
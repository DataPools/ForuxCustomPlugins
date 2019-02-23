package com.exloki.foruxmotd;

import com.exloki.foruxmotd.core.persist.GeneratedHeader;
import com.exloki.foruxmotd.core.persist.PersistedField;
import lombok.Getter;
import lombok.NoArgsConstructor;

@GeneratedHeader(value = {}, resourcePath = "header.txt")
@NoArgsConstructor
public class Settings {

    @Getter
    @PersistedField(path = "prefix", comments = {"This is the prefix"})
    private String prefixMessage = "[ForuxMOTD] ";

    @Getter
    @PersistedField(path = "motd_delay_in_seconds", comments = {"This is the delay (seconds) before the MOTD is sent to players on join"})
    private int motdDelayInSeconds = 5;
}
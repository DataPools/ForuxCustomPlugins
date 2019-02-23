package com.exloki.balancedfly;

import com.exloki.balancedfly.core.persist.GeneratedHeader;
import com.exloki.balancedfly.core.persist.PersistedField;
import com.exloki.balancedfly.core.utils.Util;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@GeneratedHeader(value = {}, resourcePath = "header.txt")
@NoArgsConstructor
public class Settings {

    @Getter
    @PersistedField(path = "prefix", comments = {"This is the prefix"})
    private String prefixMessage = "[Prefix] ";

    @Getter
    @PersistedField(path = "warmup-duration", comments = {"This defines the warmup duration in seconds"})
    private int warmupDuration = 10;

    @Getter
    @PersistedField(path = "flight-denied-regions", comments = {"This defines a list of WorldGuard region IDs that flight should be disallowed in"})
    private List<String> regionNames = Util.asList("no-fly-zone", "area-51");

    @Getter
    @PersistedField(path = "flight-denied-worlds", comments = {"This defines a list of World names that flight should be disallowed in"})
    private List<String> worldNames = Util.asList("north-korea", "mars");
}
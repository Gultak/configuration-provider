package de.gultak.discovery;

import com.google.common.base.Strings;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@Log
public class ConfigurationDiscovery {

    @Autowired
    private ContextDiscovery context;

    private ConfigurationDiscovery() {
        log.info("Configuration initializing...");
    }

    public Map<String, String> getConfiguration(@Nullable String file, @Nullable String key) {
        return context.context().getFiles().entrySet().parallelStream().filter(
                entry -> Strings.isNullOrEmpty(file) || file.equals(entry.getKey().getFileName().toString())).flatMap(
                entry -> entry.getValue().entrySet().parallelStream()).filter(
                entry -> Strings.isNullOrEmpty(key) || key.equals(entry.getKey())).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}

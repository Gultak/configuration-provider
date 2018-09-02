package de.gultak;

import de.gultak.discovery.ConfigurationDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ConfigurationController {

    @Autowired
    private ConfigurationDiscovery config;

    @RequestMapping("config")
    public Map<String, String> config(@RequestParam(required = false) @Nullable String file,
                                      @RequestParam(required = false) @Nullable String key) {
        return config.getConfiguration(file, key);
    }

}

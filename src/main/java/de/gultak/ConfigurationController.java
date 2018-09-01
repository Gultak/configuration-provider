package de.gultak;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigurationController {

    @RequestMapping("/config")
    public String config() {
        return "Hello World!";
    }

}

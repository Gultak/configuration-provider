package de.gultak.discovery;

import lombok.Getter;
import lombok.extern.java.Log;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Log
public class ContextDiscovery {

    private static final Pattern CONFIGURATION_PATTERN = Pattern.compile("^[^_].*\\.properties$");

    private static final Map<String, Context> contextCache = Collections.synchronizedMap(new HashMap<>());

    @Value("${configuration.root}")
    private Path configurationRoot;

    private ContextDiscovery() {
        log.info("ContextDiscovery initializing...");
        log.log(Level.INFO, "-----> Root-Path: {}", configurationRoot);
        if (!configurationRoot.toFile().exists()) {
            throw new IllegalArgumentException(String.format("Root-Path (%s) does not exist!", configurationRoot));
        }
    }

    public Context context() {
        String contextIdentifier = "/";
        Context context = contextCache.get(contextIdentifier);
        if (context == null) {
            synchronized (contextCache) {
                context = contextCache.get(contextIdentifier);
                if (context == null) {
                    context = initializeContext(contextIdentifier);
                    contextCache.put(contextIdentifier, context);
                }
            }
        }
        return context;
    }

    @Getter
    public static class Context {
        private final String id;
        private final Path path;
        private final Map<Path, Map<String, String>> files;

        private Context(@NotNull Path root, @NotNull String id) {
            this.id = id;
            this.path = Strings.isEmpty(id) ? root : root.resolve(id);

            if (!path.toFile().exists()) {
                throw new IllegalArgumentException(String.format("Context root (%s) does not exist!", path));
            }
            if (!path.toFile().isDirectory()) {
                throw new IllegalArgumentException(String.format("Context root (%s) is not a directory!", path));
            }
            if (!path.toFile().canRead()) {
                throw new IllegalArgumentException(String.format("Context root (%s) is not accessible!", path));
            }

            try (Stream<Path> filelist = Files.list(path)) {
                this.files = filelist.filter(entry -> !entry.toFile().isDirectory()).filter(
                        entry -> CONFIGURATION_PATTERN.matcher(entry.getFileName().toString()).matches()).
                                          collect(Collectors.toMap(Path::getFileName, this::properties));
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format("Context root (%s) is not accessible!", path), e);
            }
        }

        private Map<String, String> properties(@NotNull Path path) {
            File file = path.toFile();
            if (!file.exists()) {
                throw new IllegalArgumentException(String.format("File (%s) does not exist!", file));
            }
            if (file.isDirectory()) {
                throw new IllegalArgumentException(String.format("File (%s) is a directory!", file));
            }
            if (!file.canRead()) {
                throw new IllegalArgumentException(String.format("File (%s) is not accessible!", file));
            }
            Properties properties = new Properties();
            try (Reader reader = new FileReader(path.toFile())) {
                properties.load(reader);
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format("File (%s) is not accessible!", file), e);
            }
            return properties.entrySet().parallelStream().collect(
                    Collectors.toMap(entry -> (String) entry.getKey(), entry -> (String) entry.getValue()));
        }
    }

    private Context initializeContext(@NotNull String contextIdentifier) {
        return new Context(configurationRoot, contextIdentifier);
    }
}

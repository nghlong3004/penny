package io.nghlong3004.penny.configuration;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public final class ApplicationConfiguration {
    private final Properties properties;
    private static final String NAME_FILE = "application";
    private static final String TYPE_FILE = ".properties";
    private static final String BASE_PROP_KEY = "application.profiles.active";

    private static final ApplicationConfiguration INSTANCE = new ApplicationConfiguration();

    public static ApplicationConfiguration getInstance() {
        synchronized (INSTANCE) {
            return INSTANCE;
        }
    }

    private ApplicationConfiguration() {
        properties = new Properties();
        try {
            loadFromClasspath(getFilePath(""));
            String active = getPropertyValue(BASE_PROP_KEY);
            if (active != null && !active.isBlank()) {
                loadFromClasspath(getFilePath(active));
            }
            log.info("Loaded {} successfully", NAME_FILE);
        } catch (IOException e) {
            log.error("{} error close file: message {}", NAME_FILE + TYPE_FILE, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getTelegramToken() {
        return getPropertyValue("telegram.token");
    }

    private String getPropertyValue(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            value = null;
            log.warn("Missing property: {} ", key);
        }
        return value;
    }

    private void loadFromClasspath(String resourcePath) throws IOException {
        try (InputStream inputStream = getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                String msg = resourcePath + " not found in resource folder";
                log.error(msg);
                throw new IOException(msg);
            }
            properties.load(inputStream);
            log.debug("Merged properties from {}", resourcePath);
        }
    }

    private InputStream getResourceAsStream(String normalizedPath) {
        ClassLoader classLoader = Thread.currentThread()
                                        .getContextClassLoader();
        if (classLoader == null) {
            classLoader = ApplicationConfiguration.class.getClassLoader();
        }
        return classLoader.getResourceAsStream(Objects.requireNonNull(normalizedPath));
    }

    private String getFilePath(String active) {
        if (active == null || active.isBlank()) {
            return NAME_FILE + TYPE_FILE;
        }
        return NAME_FILE + "-" + active.trim() + TYPE_FILE;
    }

}

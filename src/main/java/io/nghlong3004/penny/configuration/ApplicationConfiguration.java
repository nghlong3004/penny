package io.nghlong3004.penny.configuration;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public final class ApplicationConfiguration {
    private final Properties properties;
    private final String nameFile = "application";
    private final String typeFile = ".properties";
    private final String environment;

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
            String BASE_PROP_KEY = "application.profiles.environment";
            environment = getPropertyValue(BASE_PROP_KEY);
            if (!environment.isBlank()) {
                loadFromClasspath(getFilePath(environment));
            }
            log.info("Loaded {} successfully", nameFile);
        } catch (IOException e) {
            log.error("{} error close file: message {}", nameFile + typeFile, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getEnvironment() {
        return environment.isBlank() ? "unknow" : environment;
    }

    public String getTelegramToken() {
        return getPropertyValue("telegram.token");
    }

    public String getDataSourceUrl() {
        return getPropertyValue("datasource.url");
    }

    public String getDataSourceDriverClassName() {
        return getPropertyValue("datasource.driver-class-name");
    }

    public String getDataSourceUsername() {
        return getPropertyValue("datasource.username");
    }

    public String getDataSourcePassword() {
        return getPropertyValue("datasource.password");
    }

    public String getMybatisPackageName() {
        return getPropertyValue("mybatis.package.name");
    }

    public String getApplicationName() {
        return getPropertyValue("application.name");
    }

    public int getApplicationPort() {
        return Integer.parseInt(getPropertyValue("application.port"));
    }

    public String getGoogleAIToken() {
        return getPropertyValue("google.ai.token");
    }

    public String getGoogleAIModel() {
        return getPropertyValue("google.ai.model");
    }

    private String getPropertyValue(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            value = "";
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
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ApplicationConfiguration.class.getClassLoader();
        }
        return classLoader.getResourceAsStream(Objects.requireNonNull(normalizedPath));
    }

    private String getFilePath(String active) {
        if (active == null || active.isBlank()) {
            return nameFile + typeFile;
        }
        return nameFile + "-" + active.trim() + typeFile;
    }

}

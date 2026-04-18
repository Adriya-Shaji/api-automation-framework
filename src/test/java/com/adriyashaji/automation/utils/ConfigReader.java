package com.adriyashaji.automation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        String env = System.getProperty("env", "local");

        // Classpath lookup — works in IDE, Maven, CI regardless of working directory
        String resourcePath = "config/" + env + ".properties";

        try (InputStream is = ConfigReader.class.getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (is == null) {
                throw new RuntimeException(
                        "Config file not found on classpath: " + resourcePath
                                + "\nExpected at: src/test/resources/" + resourcePath
                );
            }

            properties.load(is);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load config for env = " + env, e
            );
        }
    }

    public static String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException(
                    "Property '" + key + "' not found in active config file."
            );
        }
        return value;
    }
}
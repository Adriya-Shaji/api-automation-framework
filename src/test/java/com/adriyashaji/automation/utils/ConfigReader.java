package com.adriyashaji.automation.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        // Read -Denv=staging from Maven command. Default to "local" if not passed.
        String env = System.getProperty("env", "local");

        // Constructs the file path dynamically
        String filePath = "src/test/resources/config/" + env + ".properties";

        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not load config for env = " + env + '.'
                            + "\n Expected file at: " + filePath, e
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





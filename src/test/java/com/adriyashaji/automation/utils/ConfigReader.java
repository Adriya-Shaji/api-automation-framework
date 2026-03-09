package com.adriyashaji.automation.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;

    static {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream(
                    "src/test/resources/config.properties"
            );
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(
                    "config.properties not found in src/test/resources. " +
                            "Make sure the file exists before running tests.", e
            );
        }
    }

    public static String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException(
                    "Property '" + key + "' not found in config.properties"
            );
        }
        return value;
    }
}
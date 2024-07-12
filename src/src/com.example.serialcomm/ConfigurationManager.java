package com.example.serialcomm;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.*;

public class ConfigurationManager {

    private static ConfigurationManager instance;
    private static final Logger logger = Logger.getLogger(ConfigurationManager.class.getName());
    private Properties config;
    private String configFilePath;

    private ConfigurationManager(String filePath) {
        this.configFilePath = filePath;
        config = loadConfig(filePath);
    }

    public static ConfigurationManager getInstance(String filePath) {
        if (instance == null) {
            instance = new ConfigurationManager(filePath);
        }
        return instance;
    }

    private Properties loadConfig(String filePath) {
        Properties config = new Properties();
        try (InputStream input = new FileInputStream(filePath)) {
            config.load(input);
        } catch (Exception ex) {
            logger.severe("Error loading configuration: " + ex.getMessage());
        }
        return config;
    }

    public void reloadConfig() {
        config = loadConfig(configFilePath);
        logger.info("Configuration reloaded.");
    }

    public int getBaudRate() {
        return Integer.parseInt(config.getProperty("baudRate", "9600"));
    }

    public int getDataBits() {
        return Integer.parseInt(config.getProperty("dataBits", "8"));
    }

    public int getStopBits() {
        return Integer.parseInt(config.getProperty("stopBits", "1"));
    }

    public int getParity() {
        return Integer.parseInt(config.getProperty("parity", "0"));
    }
}

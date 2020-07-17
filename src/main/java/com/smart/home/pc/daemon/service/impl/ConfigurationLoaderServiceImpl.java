package com.smart.home.pc.daemon.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.smart.home.pc.daemon.dto.Config;
import com.smart.home.pc.daemon.service.ConfigurationLoaderService;

public class ConfigurationLoaderServiceImpl implements ConfigurationLoaderService {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationLoaderServiceImpl.class);

    private Gson gson = new Gson();

    private Date configurationFileLastModifiedDate;

    private Config currentUserConfiguration;

    public ConfigurationLoaderServiceImpl(String configurationPath) {
        loadConfiguration(configurationPath);
    }

    @Override
    public Config loadConfiguration(String configurationPath) {
        File configFile = new File(configurationPath);

        // Configuration is found and is a file.
        if (configFile != null && configFile.isFile()) {
            configurationFileLastModifiedDate = new Date(configFile.lastModified());
            Config loadedConfiguration = getConfigurationFromPath(configurationPath);
            if (loadedConfiguration != null) {
                currentUserConfiguration = loadedConfiguration;
                return currentUserConfiguration;
            }
        } else {
            // Configuration file not found or null
            LOGGER.error("The provided configuration path was not found");
        }
        return null;
    }

    @Override
    public boolean isConfigurationUpdated(String configurationPath) {
        File configFile = new File(configurationPath);
        if (configurationPath != null && configFile != null) {
            Date currentConfigDate = new Date(configFile.lastModified());
            if (currentConfigDate.equals(configurationFileLastModifiedDate)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Reads the configuration file as a Json and returns the corresponding object
     * 
     * @param configFilePath
     * @return
     */
    private Config getConfigurationFromPath(String configurationPath) {
        Path configFilePath = Paths.get(configurationPath);
        Config userConfiguration = null;

        Reader reader;
        try {
            reader = Files.newBufferedReader(configFilePath);

            // convert JSON file to map
            userConfiguration = gson.fromJson(reader, Config.class);
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("IOException while reading configuration file please check file format: " + configFilePath);
        }

        if (userConfiguration == null) {
            LOGGER.error("Configuration file is either empty or malformed");
        }

        return userConfiguration;
    }

    public Date getConfigurationLoadDate() {
        return configurationFileLastModifiedDate;
    }

    public void setConfigurationLoadDate(Date configurationLoadDate) {
        this.configurationFileLastModifiedDate = configurationLoadDate;
    }

    public Config getCurrentUserConfiguration() {
        return currentUserConfiguration;
    }

    public void setCurrentUserConfiguration(Config currentUserConfiguration) {
        this.currentUserConfiguration = currentUserConfiguration;
    }

}

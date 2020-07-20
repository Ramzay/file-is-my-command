package com.smart.home.pc.daemon.service.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.smart.home.pc.daemon.dto.Config;
import com.smart.home.pc.daemon.service.ConfigurationLoaderService;
import com.smart.home.pc.daemon.util.FileHelper;

public class ConfigurationLoaderServiceImpl implements ConfigurationLoaderService {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationLoaderServiceImpl.class);

    private Date configurationFileLastModifiedDate;

    private Config currentUserConfiguration;

    private FileHelper fileHelper = new FileHelper();

    public ConfigurationLoaderServiceImpl() {

    }

    public ConfigurationLoaderServiceImpl(String configurationPath) {
        loadConfiguration(configurationPath);
    }

    @Override
    public Config loadConfiguration(String configurationPath) {
        Date configurationPathLastModificationDate = fileHelper.getFileLastModificationDate(configurationPath);
        // Configuration is found and is a file.
        if (configurationPathLastModificationDate != null) {
            configurationFileLastModifiedDate = configurationPathLastModificationDate;
            Config loadedConfiguration = fileHelper.getConfigurationFromPath(configurationPath);
            if (loadedConfiguration != null) {
                currentUserConfiguration = loadedConfiguration;
                return currentUserConfiguration;
            }
        } else {
            // Configuration file not found or null
            LOGGER.error("The provided configuration path was not found" + configurationPath);
        }
        return null;
    }

    @Override
    public boolean isConfigurationUpdated(String configurationPath) {
        if (configurationPath != null) {
            Date currentConfigDate = fileHelper.getFileLastModificationDate(configurationPath);
            if (currentConfigDate == null || currentConfigDate.equals(configurationFileLastModifiedDate)) {
                return false;
            }
        }
        return true;
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

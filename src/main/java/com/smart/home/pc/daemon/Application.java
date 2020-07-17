package com.smart.home.pc.daemon;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.smart.home.pc.daemon.dto.Config;
import com.smart.home.pc.daemon.dto.FullConfiguration;
import com.smart.home.pc.daemon.impl.Daemon;
import com.smart.home.pc.daemon.service.impl.ConfigurationLoaderServiceImpl;

public class Application {

    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    private static final String CONFIGURATION_FILE_NAME = "config.json";

    public static void main(String[] args) throws RuntimeException, IOException, InterruptedException {

        LOGGER.info("Application start");
        // Determine local jar path
        Path jarAbsolutePath = FileSystems.getDefault().getPath("").toAbsolutePath();

        // Load configuration file
        String initialUserConfigurationPath = jarAbsolutePath.toString() + File.separator + CONFIGURATION_FILE_NAME;
        ConfigurationLoaderServiceImpl configurationLoaderService = new ConfigurationLoaderServiceImpl(initialUserConfigurationPath);
        Config initialUserConfiguration = configurationLoaderService.getCurrentUserConfiguration();

        // Configuration not found we exit the app for now
        if (initialUserConfiguration == null) {
            LOGGER.error("Configuration file is either empty or malformed, exiting...");
            System.exit(1);
        }

        // Create script directory
        if (StringUtils.isBlank(initialUserConfiguration.getScriptDir())) {
            LOGGER.warn("Missing scriptDir property in configuration file. Will be using 'script' dir");
            initialUserConfiguration.setScriptDir("script");
        }

        Path batchPath = Paths.get(jarAbsolutePath + File.separator + initialUserConfiguration.getScriptDir() + File.separator);
        LOGGER.info("Script location:" + batchPath);

        try {
            Files.createDirectories(batchPath);
        } catch (IOException e) {
            LOGGER.error("Coudln't create directory" + jarAbsolutePath);
            e.printStackTrace();
            System.exit(1);
        }

        // Initialise and run Daemon
        FullConfiguration configuration = new FullConfiguration();
        configuration.setUserConfiguration(initialUserConfiguration);
        configuration.setAbsPathHomeFolder(jarAbsolutePath.toString());
        configuration.setAbsPathScriptFolder(configuration.getAbsPathHomeFolder() + File.separator + initialUserConfiguration.getScriptDir() + File.separator);
        
        Daemon deamon = new Daemon();
        while (true) {

            // New configuration detected
            if (configurationLoaderService.isConfigurationUpdated(initialUserConfigurationPath)) {

                if (configuration.getUserConfiguration().isLogEverything()) {
                    LOGGER.info("New configuration detected !");
                }
                // Load configuration
                Config newUserConfiguration = configurationLoaderService.loadConfiguration(initialUserConfigurationPath);
                if (newUserConfiguration != null) {
                    configuration.setUserConfiguration(newUserConfiguration);
                } else {
                    LOGGER.warn("Cound not load new configuration, will keep using the old one !");
                }
            }

            deamon.run(configuration);
            Thread.sleep(10000);
        }

    }
}

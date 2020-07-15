package com.smart.home.pc.daemon;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.smart.home.pc.daemon.dto.Config;
import com.smart.home.pc.daemon.dto.FullConfiguration;
import com.smart.home.pc.daemon.impl.Daemon;

public class Application {

    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    private static final String CONFIGURATION_FILE_NAME = "config.json";

    public static void main(String[] args) throws RuntimeException, IOException, InterruptedException {

        LOGGER.info("Application start");
        // Determine local jar path
        Path jarAbsolutePath = FileSystems.getDefault().getPath("").toAbsolutePath();

        // Check and create configuration file if not found
        String configPath = jarAbsolutePath.toString() + File.separator + CONFIGURATION_FILE_NAME;
        Path configFilePath = Paths.get(configPath);
        File configFile = new File(configPath);

        // Create configuration file
        if (!configFile.isFile()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                LOGGER.error("Could not create the configuration file: " + configFilePath.toString());
                e.printStackTrace();
                System.exit(1);
            }
        }

        if (configFile.isFile()) {
            LOGGER.info("Configuration location: " + configPath);
        }

        // create Gson instance
        Gson gson = new Gson();
        Config daemonConfig = new Config();

        // create a reader
        Reader reader;
        try {
            reader = Files.newBufferedReader(configFilePath);

            // convert JSON file to map
            daemonConfig = gson.fromJson(reader, Config.class);
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Configuration not found we exit the app for now
        if (daemonConfig == null) {
            LOGGER.error("Configuration file is either empty or malformed, exiting...");
            System.exit(1);
        }
        // Create script directory
        if (StringUtils.isBlank(daemonConfig.getScriptDir())) {
            LOGGER.warn("Missing scriptDir property in configuration file. Will be using 'script' dir");
            daemonConfig.setScriptDir("script");
        }
        Path batchPath = Paths.get(jarAbsolutePath + File.separator + daemonConfig.getScriptDir() + File.separator);
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
        configuration.setUserConfiguration(daemonConfig);
        configuration.setAbsPathHomeFolder(jarAbsolutePath.toString());
        configuration.setAbsPathScriptFolder(configuration.getAbsPathHomeFolder() + File.separator + daemonConfig.getScriptDir() + File.separator);
        Daemon deamon = new Daemon(configuration);
        while (true) {
            deamon.run();
            Thread.sleep(10000);
        }

    }
}

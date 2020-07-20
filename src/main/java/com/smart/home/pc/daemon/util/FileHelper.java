package com.smart.home.pc.daemon.util;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.smart.home.pc.daemon.dto.Config;

/**
 * Helper class to manage file actions
 * 
 * @author Ramzi
 *
 */
public class FileHelper {
    
    private static final Logger LOGGER = LogManager.getLogger(FileHelper.class);
    
    private Gson gson = new Gson();

    /**
     * Get last modification date for the given file path
     * 
     * @param filePath, String containing the absolute path of the file
     * @return last modification date or null
     */
    public Date getFileLastModificationDate(String fileAbsPath) {
        if (StringUtils.isEmpty(fileAbsPath)) {
            return null;
        }

        File configFile = new File(fileAbsPath);
        if (configFile.exists()) {
            return new Date(configFile.lastModified());
        }

        return null;
    }
    
    /**
     * Reads the configuration file as a Json and returns the corresponding object
     * 
     * @param configFilePath
     * @return
     */
    public Config getConfigurationFromPath(String configurationPath) {
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

}

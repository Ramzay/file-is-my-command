package com.smart.home.pc.daemon.util;

import java.io.File;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper class to manage file actions
 * 
 * @author Ramzi
 *
 */
public class FileHelper {

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

}

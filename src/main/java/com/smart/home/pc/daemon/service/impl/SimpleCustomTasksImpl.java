package com.smart.home.pc.daemon.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.smart.home.pc.daemon.dto.Command;
import com.smart.home.pc.daemon.dto.FullConfiguration;
import com.smart.home.pc.daemon.service.SimpleCustomTasks;

public class SimpleCustomTasksImpl implements SimpleCustomTasks {

    private static final Logger LOGGER = LogManager.getLogger(SimpleCustomTasksImpl.class);

    public void executeCommand(FullConfiguration configuation, Command command) throws RuntimeException, IOException {
        String operatingSystem = System.getProperty("os.name");
        if ("Linux".equals(operatingSystem) || "Mac OS X".equals(operatingSystem)) {
            String shutdownCommand = "shutdown -h now";
            Runtime.getRuntime().exec(shutdownCommand);
        } else if (isWindowsOS(operatingSystem)) { // Windows
            executWindowsProcedure(configuation, command);
        } else {
            LOGGER.error("Unsupported operating system.");
        }
    }

    /**
     * Will detect if the operating system name corresponds to Windows
     * 
     * @param osName: OS name
     * @return true if windows OS
     */
    private boolean isWindowsOS(String osName) {
        if (!StringUtils.isEmpty(osName) && osName.toLowerCase().contains("windows")) {
            return true;
        }
        return false;
    }

    /**
     * Will perform the windows tasks, and will attempt multiple times before
     * stopping attempt
     */
    private void executWindowsProcedure(FullConfiguration configuration, Command command) {
        int maxAttempts = 5;
        int attempts = 0;
        boolean processInSuccess = false;
        while (!processInSuccess && attempts < maxAttempts) {
            processInSuccess = executeBatchInBatchDir(command.getScript(), configuration);
            attempts++;
            sleep(5000);
        }

        if (attempts >= maxAttempts || !processInSuccess) {
            LOGGER.error("Windows process failed to complete suceefully after: " + attempts + " attempts, silently skipping.");
        }

    }

    /**
     * Function that will execute the batch located in the default batch directory
     * 
     * @param batchName
     * @return
     */
    private boolean executeBatchInBatchDir(String batchName, FullConfiguration configuration) {
        boolean executionSuccess = false;
        BufferedReader br = null;
        int cmdExecutionAttempt = 2;
        try {

            br = Files.newBufferedReader(Paths.get(configuration.getAbsPathScriptFolder() + batchName));

            String line;
            while ((line = br.readLine()) != null) {
                boolean lineProcessed = false;
                int currentCmdExecutionAttempt = 0;
                while (!lineProcessed) {
                    // Perform line by line CMD execution
                    try {
                        Process currentProc = Runtime.getRuntime().exec("cmd /c" + line);
                        currentProc.waitFor(); // Wait for execution
                        if (currentProc.exitValue() == 0 || currentCmdExecutionAttempt > cmdExecutionAttempt) { // Exit code OK, OR max attempt reached
                            lineProcessed = true;
                        } else { // Exit code KO
                            if (currentProc.exitValue() == 128 && line != null && line.toLowerCase().contains("taskkill")) { // When process not running taskkill returns code 128
                                lineProcessed = true;
                            } else {
                                LOGGER.error(line + " EXITED WITH ERROR CODE: " + currentProc.exitValue()); // To better handle future errors
                            }
                        }
                    } catch (Exception e) { // If exception occurred during execution increment attempt and log error
                        LOGGER.error(line + "ERROR WITH BELOW STACK");
                        e.printStackTrace();
                        if (currentCmdExecutionAttempt > cmdExecutionAttempt) {
                            lineProcessed = true;
                        }
                    }

                    if (!lineProcessed) {
                        currentCmdExecutionAttempt++;
                        sleep(1000);
                    }
                }
            }
            executionSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return executionSuccess;
    }

    /**
     * Helper function that will parse and execute a batch file located in
     * /resources folder we use the buffer reader in order to be compatible with jar
     * packaging
     * 
     * @param batchName: batch file name
     */
    private boolean executeBatchInResources(String batchName) {
        boolean executionSuccess = false;
        BufferedReader br = null;
        int cmdExecutionAttempt = 2;
        try {
            InputStream in = getClass().getResourceAsStream("/" + batchName);
            br = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = br.readLine()) != null) {
                boolean lineProcessed = false;
                int currentCmdExecutionAttempt = 0;
                while (!lineProcessed) {
                    // Perform line by line CMD execution
                    try {
                        Process currentProc = Runtime.getRuntime().exec("cmd /c" + line);
                        currentProc.waitFor(); // Wait for execution
                        if (currentProc.exitValue() == 0 || currentCmdExecutionAttempt > cmdExecutionAttempt) { // Exit code OK, OR max attempt reached
                            lineProcessed = true;
                        } else { // Exit code KO
                            if (currentProc.exitValue() == 128 && line != null && line.toLowerCase().contains("taskkill")) { // When process not running taskkill returns code 128
                                lineProcessed = true;
                            } else {
                                LOGGER.error(line + " EXITED WITH ERROR CODE: " + currentProc.exitValue()); // To better handle future errors
                            }
                        }
                    } catch (Exception e) { // If exception occurred during execution increment attempt and log error
                        LOGGER.error(line + "ERROR WITH BELOW STACK");
                        e.printStackTrace();
                        if (currentCmdExecutionAttempt > cmdExecutionAttempt) {
                            lineProcessed = true;
                        }
                    }

                    if (!lineProcessed) {
                        currentCmdExecutionAttempt++;
                        sleep(1000);
                    }
                }
            }
            executionSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return executionSuccess;
    }

    /**
     * Helper function to sleep and remove exception handling
     * 
     * @param milliseconds
     */
    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

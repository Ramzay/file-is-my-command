package com.smart.home.pc.daemon.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.smart.home.pc.daemon.dto.Command;
import com.smart.home.pc.daemon.dto.FullConfiguration;
import com.smart.home.pc.daemon.service.SimpleCustomTasks;
import com.smart.home.pc.daemon.service.impl.SimpleCustomTasksImpl;

public class Daemon {

    private static final Date PROC_START_DATE = new Date();

    private SimpleCustomTasks simpleCustomTasks;

    public Daemon() {
        simpleCustomTasks = new SimpleCustomTasksImpl();
    }

    public void run(FullConfiguration configuration) throws RuntimeException, IOException {
        // Initialise
        initProc(configuration);

        // Run proc
        Command command = getCommand(configuration);
        if (command != null) {
            // Delete files associated to this command
            deleteAllFiles(command.getFileName(), configuration);
            // Execute shutdown action
            simpleCustomTasks.executeCommand(configuration, command);
        }

        // Final proc
    }

    /**
     * Initialisation for the process: - Delete all files older than the process
     * start date time
     */
    private void initProc(FullConfiguration configuration) {
        // Delete all files that are older than the start procedure date
        File[] filesInCommandFolder = getFilesInCommandDirLocation(configuration);
        if (filesInCommandFolder != null) {
            for (int i = 0; i < filesInCommandFolder.length; i++) {
                File currentFile = filesInCommandFolder[i];
                if (currentFile != null && currentFile.isFile()) {
                    Date fileLastModified = new Date(currentFile.lastModified());
                    if (PROC_START_DATE.after(fileLastModified)) {
                        currentFile.delete();
                    }
                }
            }
        }
    }

    /**
     * Function that will check if there are any pending actions to perform based on
     * file name located in special directory if specific file is detected we return
     * the underlying action to perform
     * 
     * @return Command : command to be performed
     */
    private Command getCommand(FullConfiguration configuration) {

        // Directory exists we check files inside
        File[] listOfFiles = getFilesInCommandDirLocation(configuration);
        if (listOfFiles != null && listOfFiles.length == 0) {
            return null;
        }

        // Check if there is a trigger file in the directory
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) { // Make sure it's a file and not a dir
                for (Command currentCommand : configuration.getUserConfiguration().getCommands()) {
                    if (currentCommand.getFileName() != null && listOfFiles[i].getName().contains(currentCommand.getFileName())) {
                        return currentCommand; // TODO: list of commands ?
                    }
                }
            }
        }
        return null;
    }

    /**
     * Deletes all files that has the given parameter included in their name
     * 
     * @param name string that is used to find the files to delete
     */
    private void deleteAllFiles(String name, FullConfiguration configuration) {
        // Delete all files that contains the command in parameter
        File[] filesInCommandFolder = getFilesInCommandDirLocation(configuration);
        if (filesInCommandFolder != null) {
            for (int i = 0; i < filesInCommandFolder.length; i++) {
                File currentFile = filesInCommandFolder[i];
                if (currentFile != null && currentFile.isFile()) {
                    if (currentFile.getName().toLowerCase().contains(name.toLowerCase())) {
                        currentFile.delete();
                    }
                }
            }
        }
    }

    /**
     * Function that will check base folder existence
     * 
     * @return files in base directory if it exists otherwise null
     */
    private File[] getFilesInCommandDirLocation(FullConfiguration configuration) {
        File folder = new File(configuration.getUserConfiguration().getCommandPath());
        if (folder != null && folder.exists() && folder.isDirectory()) {
            return folder.listFiles();
        }
        return new File[0];
    }

}

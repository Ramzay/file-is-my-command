package com.smart.home.pc.daemon.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.smart.home.pc.daemon.dto.FullConfiguration;
import com.smart.home.pc.daemon.service.SimpleCustomTasks;
import com.smart.home.pc.daemon.service.impl.SimpleCustomTasksImpl;

public class Daemon {

	private static final String SHUTDOWN_FILE_COMMAND = "shutdown-bedroom"; // file name that triggers an action

	private static final String SHUTDOWN_COMMAND = "SHUTDOWN"; // TODO: enum or configurable

	private static final Date PROC_START_DATE = new Date();

	// private String absHomeDir;

	// private String scriptDirName;

	private String commandDirAbsPath;

	private SimpleCustomTasks simpleCustomTasks;

	private FullConfiguration configuration;

	public Daemon(FullConfiguration configuration) {
		// absHomeDir = configuration.getAbsPathHomeFolder();
		// scriptDirName = configuration.getUserConfiguration().getScriptDir();
		commandDirAbsPath = configuration.getUserConfiguration().getCommandPath();
		simpleCustomTasks = new SimpleCustomTasksImpl();
		this.configuration = configuration;
	}

	public void run() throws RuntimeException, IOException {
		// Initialise
		initProc();

		// Run proc
		String command = getCommand();
		if (!StringUtils.isEmpty(command)) {
			// Check which command and perform action
			if (SHUTDOWN_COMMAND.equals(command)) {
				// Delete command
				deleteAllFiles(SHUTDOWN_FILE_COMMAND);
				// Execute shutdown action
				simpleCustomTasks.shutdown(configuration);
			}
		}

		// Final proc
	}

	/**
	 * Initialisation for the process: - Delete all files older than the process
	 * start date time
	 */
	private void initProc() {
		// Delete all files that are older than the start proc date
		File[] filesInCommandFolder = getFilesInCommandDirLocation();
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
	 * @return String : action to be performed
	 */
	private String getCommand() {

		// Directory exists we check files inside
		File[] listOfFiles = getFilesInCommandDirLocation();
		if (listOfFiles != null && listOfFiles.length == 0) {
			return null;
		}

		// Loop in file list and we will check if one of them is OK
		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {
				if (listOfFiles[i].getName().contains(SHUTDOWN_FILE_COMMAND)) {
					return SHUTDOWN_COMMAND;
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
	private void deleteAllFiles(String name) {
		// Delete all files that contains the command in parameter
		File[] filesInCommandFolder = getFilesInCommandDirLocation();
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
	private File[] getFilesInCommandDirLocation() {
		File folder = new File(commandDirAbsPath);
		if (folder != null && folder.exists() && folder.isDirectory()) {
			return folder.listFiles();
		}
		return new File[0];
	}

}

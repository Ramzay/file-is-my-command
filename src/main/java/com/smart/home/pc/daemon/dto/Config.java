package com.smart.home.pc.daemon.dto;

public class Config {
	
	private String commandPath;
	
	private String sleepBatch;
	
	private String scriptDir;

	public String getScriptDir() {
		return scriptDir;
	}

	public void setScriptDir(String scriptDir) {
		this.scriptDir = scriptDir;
	}

	public String getSleepBatch() {
		return sleepBatch;
	}

	public void setSleepBatch(String sleepBatch) {
		this.sleepBatch = sleepBatch;
	}

	public String getCommandPath() {
		return commandPath;
	}

	public void setCommandPath(String commandPath) {
		this.commandPath = commandPath;
	}

}

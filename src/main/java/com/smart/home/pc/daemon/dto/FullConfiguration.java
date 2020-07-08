package com.smart.home.pc.daemon.dto;

public class FullConfiguration  {

	private String absPathScriptFolder;
	
	private String absPathHomeFolder;
	
	private Config userConfiguration;

	public Config getUserConfiguration() {
		return userConfiguration;
	}

	public void setUserConfiguration(Config userConfiguration) {
		this.userConfiguration = userConfiguration;
	}

	public String getAbsPathScriptFolder() {
		return absPathScriptFolder;
	}

	public void setAbsPathScriptFolder(String absPathScriptFolder) {
		this.absPathScriptFolder = absPathScriptFolder;
	}

	public String getAbsPathHomeFolder() {
		return absPathHomeFolder;
	}

	public void setAbsPathHomeFolder(String absPathHomeFolder) {
		this.absPathHomeFolder = absPathHomeFolder;
	}
}

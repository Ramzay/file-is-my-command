package com.smart.home.pc.daemon.dto;

import java.util.List;

public class Config {

    private String commandPath;

    private String scriptDir;

    private List<Command> commands;

    private boolean logEverything = false;

    public String getScriptDir() {
        return scriptDir;
    }

    public void setScriptDir(String scriptDir) {
        this.scriptDir = scriptDir;
    }

    public String getCommandPath() {
        return commandPath;
    }

    public void setCommandPath(String commandPath) {
        this.commandPath = commandPath;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public boolean isLogEverything() {
        return logEverything;
    }

    public void setLogEverything(boolean logEverything) {
        this.logEverything = logEverything;
    }

}

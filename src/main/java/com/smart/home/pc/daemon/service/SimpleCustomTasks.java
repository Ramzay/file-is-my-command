package com.smart.home.pc.daemon.service;

import java.io.IOException;

import com.smart.home.pc.daemon.dto.Command;
import com.smart.home.pc.daemon.dto.FullConfiguration;

public interface SimpleCustomTasks {
    public void executeCommand(FullConfiguration configuration, Command command) throws RuntimeException, IOException;
}

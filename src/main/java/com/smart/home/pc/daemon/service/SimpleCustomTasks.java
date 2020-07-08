package com.smart.home.pc.daemon.service;

import java.io.IOException;

import com.smart.home.pc.daemon.dto.FullConfiguration;

public interface SimpleCustomTasks {
    public void shutdown(FullConfiguration configuration) throws RuntimeException, IOException;
}

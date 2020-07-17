package com.smart.home.pc.daemon.service;

import com.smart.home.pc.daemon.dto.Config;

public interface ConfigurationLoaderService {

    public Config loadConfiguration(String configurationPath);

    public boolean isConfigurationUpdated(String configurationPath);

}

package com.smart.home.pc.daemon.service.impl;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.smart.home.pc.daemon.dto.Config;
import com.smart.home.pc.daemon.util.FileHelper;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationLoaderServiceTest {

    @InjectMocks
    ConfigurationLoaderServiceImpl configurationLoaderServiceImpl = new ConfigurationLoaderServiceImpl();

    @Mock
    FileHelper fileHelper;

    @Test
    public void isConfigurationUpdatedInitialLoadTest() {
        /*
         * Initial configuration, new configuration has a last modification date (file
         * FOUND)
         * 
         */

        configurationLoaderServiceImpl.setConfigurationLoadDate(null);
        Mockito.when(fileHelper.getFileLastModificationDate("")).thenReturn(new Date());
        Assert.assertTrue(configurationLoaderServiceImpl.isConfigurationUpdated(""));

        /*
         * Initial configuration, new configuration has a last modification date (file
         * NOT FOUND)
         * 
         */
        configurationLoaderServiceImpl.setConfigurationLoadDate(null);
        Mockito.when(fileHelper.getFileLastModificationDate("")).thenReturn(null);
        Assert.assertFalse(configurationLoaderServiceImpl.isConfigurationUpdated(""));
    }

    @Test
    public void isConfigurationUpdatedAfterInitialLoadTest() {
        /*
         * Initial configuration is SET we will play with the new configuration last
         * modification test
         */
        Date lastModificationDate = new Date();
        Date before = new Date(lastModificationDate.getTime() - 10);
        Date after = new Date(lastModificationDate.getTime());

        // New configuration file modification date is same as initial configuration
        configurationLoaderServiceImpl.setConfigurationLoadDate(lastModificationDate);
        Mockito.when(fileHelper.getFileLastModificationDate("")).thenReturn(lastModificationDate);
        Assert.assertFalse(configurationLoaderServiceImpl.isConfigurationUpdated(""));

        // New configuration file modification date is null
        configurationLoaderServiceImpl.setConfigurationLoadDate(new Date());
        Mockito.when(fileHelper.getFileLastModificationDate("")).thenReturn(null);
        Assert.assertFalse(configurationLoaderServiceImpl.isConfigurationUpdated(""));

        // New configuration last modification is in the future
        configurationLoaderServiceImpl.setConfigurationLoadDate(after); // Set last modification
        Mockito.when(fileHelper.getFileLastModificationDate("")).thenReturn(before);
        Assert.assertTrue(configurationLoaderServiceImpl.isConfigurationUpdated(""));

        // New configuration last modification is in the past
        configurationLoaderServiceImpl.setConfigurationLoadDate(before); // Set last modification
        Mockito.when(fileHelper.getFileLastModificationDate("")).thenReturn(after);
        Assert.assertTrue(configurationLoaderServiceImpl.isConfigurationUpdated(""));

    }

    @Test
    public void loadConfigurationTestEmpty() {
        Assert.assertNull(configurationLoaderServiceImpl.loadConfiguration(null));
        Assert.assertNull(configurationLoaderServiceImpl.loadConfiguration(""));
        Assert.assertNull(configurationLoaderServiceImpl.loadConfiguration("   "));
    }

    @Test
    public void loadConfigurationTest() {

        String dummyConfigFilePath = "dummyConfigFilePath";
        String dummyCommandPath = "dummyCommandPath";
        Date lastModificationDate = new Date();
        Date lastModificationDatePast = new Date(lastModificationDate.getTime() - 10);
        Config expectedConfiguration = new Config();
        expectedConfiguration.setCommandPath(dummyCommandPath);

        // Configuration file not found
        Mockito.when(fileHelper.getFileLastModificationDate(dummyConfigFilePath)).thenReturn(null);
        // Execute loading
        Assert.assertNull(configurationLoaderServiceImpl.loadConfiguration(dummyConfigFilePath));

        // Configuration file found but issue in the loading
        configurationLoaderServiceImpl.setConfigurationLoadDate(lastModificationDatePast); // Set last configuration modification date in the past
        configurationLoaderServiceImpl.setCurrentUserConfiguration(null);
        Mockito.when(fileHelper.getFileLastModificationDate(dummyConfigFilePath)).thenReturn(lastModificationDate);
        Mockito.when(fileHelper.getConfigurationFromPath(dummyConfigFilePath)).thenReturn(null); // Issue in loading the configuration object
        // Execute loading
        Assert.assertNull(configurationLoaderServiceImpl.loadConfiguration(dummyConfigFilePath));
        // Modification date should not be altered
        Assert.assertFalse(configurationLoaderServiceImpl.getConfigurationLoadDate().equals(lastModificationDate));
        Assert.assertTrue(configurationLoaderServiceImpl.getConfigurationLoadDate().equals(lastModificationDatePast));

        // Configuration file found and loaded
        configurationLoaderServiceImpl.setConfigurationLoadDate(lastModificationDatePast); // Set last configuration modification date in the past
        configurationLoaderServiceImpl.setCurrentUserConfiguration(null);
        Mockito.when(fileHelper.getFileLastModificationDate(dummyConfigFilePath)).thenReturn(lastModificationDate);
        Mockito.when(fileHelper.getConfigurationFromPath(dummyConfigFilePath)).thenReturn(expectedConfiguration);
        // Execute loading
        Assert.assertNotNull(configurationLoaderServiceImpl.loadConfiguration(dummyConfigFilePath));
        // Modification date should be updated
        Assert.assertTrue(configurationLoaderServiceImpl.getConfigurationLoadDate().equals(lastModificationDate));
        // Current configuration should be updated
        Assert.assertTrue(configurationLoaderServiceImpl.getCurrentUserConfiguration().getCommandPath().equals(dummyCommandPath));

    }

}

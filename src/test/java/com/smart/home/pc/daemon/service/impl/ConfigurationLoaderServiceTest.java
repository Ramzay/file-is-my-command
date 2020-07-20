package com.smart.home.pc.daemon.service.impl;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.smart.home.pc.daemon.util.FileHelper;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationLoaderServiceTest {

    @InjectMocks
    ConfigurationLoaderServiceImpl configurationLoaderServiceImpl = new ConfigurationLoaderServiceImpl();

    @Mock
    FileHelper fileHelper;

    @Test
    public void isConfigurationUpdatedTest() {
        // Initial configuration, new configuration has a last modification date (file FOUND)
        configurationLoaderServiceImpl.setConfigurationLoadDate(null);
        Mockito.when(fileHelper.getFileLastModificationDate("")).thenReturn(new Date());
        Assert.assertTrue(configurationLoaderServiceImpl.isConfigurationUpdated(""));
        
        // Initial configuration, new configuration has no modification date (file NOT FOUND)
        configurationLoaderServiceImpl.setConfigurationLoadDate(null);
        Mockito.when(fileHelper.getFileLastModificationDate("")).thenReturn(null);
        Assert.assertFalse(configurationLoaderServiceImpl.isConfigurationUpdated(""));

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

}

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
    public void isConfigurationUpdatedNewConfiguration() {
        // New configuration (nothing prior)
        Assert.assertTrue(configurationLoaderServiceImpl.isConfigurationUpdated(""));

        // New configuration (current configuration was previously set)
        configurationLoaderServiceImpl.setConfigurationLoadDate(new Date());
        Assert.assertTrue(configurationLoaderServiceImpl.isConfigurationUpdated(""));

        // No new configuration (same date of modification)
        Date lastModificationDate = new Date();
        configurationLoaderServiceImpl.setConfigurationLoadDate(lastModificationDate); // Set last modification
        Mockito.when(fileHelper.getFileLastModificationDate("")).thenReturn(lastModificationDate); // Mock response of file helper to the same date
        Assert.assertFalse(configurationLoaderServiceImpl.isConfigurationUpdated(""));

       
        Date before = new Date(lastModificationDate.getTime() - 10);
        Date after = new Date(lastModificationDate.getTime());
       
        // last modification is in the future so we should not update the configuration
        configurationLoaderServiceImpl.setConfigurationLoadDate(after); // Set last modification
        Mockito.when(fileHelper.getFileLastModificationDate("")).thenReturn(before); 
        Assert.assertFalse(configurationLoaderServiceImpl.isConfigurationUpdated(""));

        // last modification is in the past so we should update the configuration
        configurationLoaderServiceImpl.setConfigurationLoadDate(before); // Set last modification
        Mockito.when(fileHelper.getFileLastModificationDate("")).thenReturn(after); 
        Assert.assertTrue(configurationLoaderServiceImpl.isConfigurationUpdated(""));

    }

}

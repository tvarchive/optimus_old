package com.testvagrant.optimus.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;


public class IOSLogFinderTest {

    @Test
    public void webDriverAgentSessionFolderShouldNotBeEmpty() {
        IOSLogFinder logFinder = new IOSLogFinder();
        Optional<String> folderName = logFinder.getFolderName();
        Assert.assertEquals(folderName.isPresent(),true);
    }

    @Test
    public void retriveAllFoldersGreaterThanLastModified() throws IOException {
        IOSLogFinder logFinder = new IOSLogFinder();
        CurrentTime.setCurrentModifiedTime(new Date().getTime());
        WDAServerManager wdaServerManager  = new WDAServerManager("40EB53D0-B4AE-44EE-834D-9B543E5D8FDC");
        wdaServerManager.startServer();
        List<File> log = logFinder.findActiveWebDriverSessions();
        Assert.assertEquals(1,log.size());
    }


    @Test
    public void findStandardOutTxt() throws IOException {
        IOSLogFinder logFinder = new IOSLogFinder();
        CurrentTime.setCurrentModifiedTime(new Date().getTime());
        WDAServerManager wdaServerManager  = new WDAServerManager("40EB53D0-B4AE-44EE-834D-9B543E5D8FDC");
        wdaServerManager.startServer();
        wdaServerManager.getServerUrl();
        String serverUrl = wdaServerManager.getServerUrl();
        System.out.println(serverUrl);
    }


}

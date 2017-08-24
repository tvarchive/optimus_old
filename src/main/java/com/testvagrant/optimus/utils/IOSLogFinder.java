package com.testvagrant.optimus.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by krishnanand on 31/07/17.
 */
public class IOSLogFinder {
    private static final String BASEPATH = "/var/folders/jc/";
    private static final String WEBDRIVERAGENT_PATH = "/var/folders/jc/%s/T/com.apple.dt.XCTest";
    private static int currentWebDriverAgentSessions;
    public IOSLogFinder() {
        setCurrentWebDriverAgentSessions(getWebDriverAgentSessionCount());
    }

    public static int getCurrentWebDriverAgentSessions() {
        return currentWebDriverAgentSessions;
    }

    public static void setCurrentWebDriverAgentSessions(int currentWebDriverAgentSessions) {
        IOSLogFinder.currentWebDriverAgentSessions = currentWebDriverAgentSessions;
    }

    public List<File> findActiveWebDriverSessions() {
        List<File> collect = getWebDriverAgentSessions();
        List<File> activeWebDriverSessions = collect.stream().filter(file1 -> file1.lastModified() > CurrentTime.getCurrentModifiedTime()).collect(Collectors.toList());
        return activeWebDriverSessions;
    }

    public int getWebDriverAgentSessionCount() {
        return getWebDriverAgentSessions().size();
    }


    public List<File> getWebDriverAgentSessions() {
        Optional<String> folderName = getFolderName();
        List<File> collect = null;
        if(folderName.isPresent()) {
            File file = new File(String.format(WEBDRIVERAGENT_PATH,folderName.get()));
            if(file.exists()) {
                File[] files = file.listFiles();
                collect = Arrays.asList(files);
//                collect = Arrays.stream(files).filter(file1 -> file1.lastModified() > CurrentTime.getCurrentModifiedTime()).collect(Collectors.toList());
            }
        }
        return collect;
    }


    public Optional<String> getFolderName() {
        File file  = new File(BASEPATH);
        final Optional<String>[] fileName = new Optional[]{Optional.empty()};
        if(file.exists()) {
            File[] files = file.listFiles();
            Arrays.stream(files).forEach(file1 -> {
                if(file1.exists()) {
                    fileName[0] =  Optional.of(file1.getName());
                }
            });

        }
        return fileName[0];
    }

}

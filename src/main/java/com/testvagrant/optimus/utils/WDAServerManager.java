package com.testvagrant.optimus.utils;


import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;

public class WDAServerManager {

    private String udid;
    private String serverUrl;
    private boolean fileFound;
    private File fileToSearch;
    private int port;

    public WDAServerManager(String udid) {
        this.udid = udid;
    }

    public String getServerUrl() {
        findServerUrl();
        return serverUrl;
    }

    public int getPort() {
        System.out.println(serverUrl);
        try {
            if(serverUrl==null) {
                findServerUrl();
            }
            URI uri = new URI(serverUrl);
            port = uri.getPort();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return port;
    }

    public void startServer() throws IOException {
        List<String> command = getCommand();
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File("/Users/krishnanand/Development/personal_projects/WebDriverAgent"));
        processBuilder.command(command);
        Process process = processBuilder.start();
        if(process.isAlive()){
            System.out.println("Started server for udid = "+udid);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            await().atMost(20, TimeUnit.SECONDS).until(() -> IOSLogFinder.getCurrentWebDriverAgentSessions() < new IOSLogFinder().getWebDriverAgentSessionCount());
        }
    }

    private void findServerUrl() {
        IOSLogFinder logFinder = new IOSLogFinder();
        List<File> activeWebDriverSessions = logFinder.getWebDriverAgentSessions();
        System.out.println("Active webdriver sessions are "+activeWebDriverSessions.size());
       activeWebDriverSessions.stream().forEach(sessionFolder -> {
           collectFile(sessionFolder,"Session-WebDriverAgentRunner",".log");
           try {
               await().atMost(20, TimeUnit.SECONDS).until(() -> Files.readLines(fileToSearch, Charset.defaultCharset()).stream().anyMatch(line -> line.contains("id=" + udid)));
               collectFile(sessionFolder, "StandardOutput", ".txt");
               try {
                   await().atMost(20, TimeUnit.SECONDS).until(() -> Files.readLines(fileToSearch, Charset.defaultCharset()).stream().anyMatch(line -> line.contains("ServerURLHere")));
                   List<String> strings = Files.readLines(fileToSearch, Charset.defaultCharset());
                   Optional<String> serverURLHere = strings.stream().filter(line -> line.contains("ServerURLHere")).findFirst();
                   if(serverURLHere.isPresent()) {
                       String s = serverURLHere.get();
                       int left = s.indexOf("->");
                       int right = s.indexOf("<-");
                       serverUrl = s.substring(left,right).replace("->","");
                   }

               } catch (IOException e) {

               }
           } catch (Exception e) {

           }
       });
    }

    private void collectFile(File rootFile, String fileName, String fileExtensionToSearch) {
        if(rootFile.exists() && rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            assert files != null;
            for(File file : files) {
                if(!file.isDirectory()) {
                    fileFound = file.getName().contains(fileName) && file.getName().endsWith(fileExtensionToSearch);
                    if(fileFound) {
                        fileToSearch = file;
                        break;
                    }
                } else {
                    collectFile(file,fileName,fileExtensionToSearch);
                }
            }
        }
    }

    private List<String> getCommand() {
        List<String> command = new ArrayList<>();
        command.add("xcodebuild");
        command.add("-project");
        command.add("WebDriverAgent.xcodeproj");
        command.add("-scheme");
        command.add("WebDriverAgentRunner");
        command.add("-destination");
        command.add(String.format("id=%s",udid));
        command.add("test");
        return command;
    }

}

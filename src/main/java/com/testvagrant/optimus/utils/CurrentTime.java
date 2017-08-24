package com.testvagrant.optimus.utils;


public class CurrentTime {

    private static long currentModifiedTime;

    public static long getCurrentModifiedTime() {
        return currentModifiedTime;
    }

    public static void setCurrentModifiedTime(long currentModifiedTime) {
        CurrentTime.currentModifiedTime = currentModifiedTime;
    }
}

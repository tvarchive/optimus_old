package com.testvagrant.optimus.utils;

import com.testvagrant.commons.entities.device.OSVersion;
import com.testvagrant.commons.entities.device.Platform;
import com.testvagrant.mdb.enums.AOSVersion;
import com.testvagrant.mdb.enums.IOSVersion;
import com.testvagrant.optimus.exceptions.UnsupportedOSException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Optional;

public class Commons {

    public OSVersion getOsVerion(Platform platform, String osVersion) {
        OSVersion os = null;
        switch (platform) {
            case ANDROID:
                Optional<AOSVersion> first = Arrays.stream(AOSVersion.values()).filter(version -> version.getVersion().equals(osVersion)).findFirst();
                if(first.isPresent()) {
                    os =  first.get();
                }
                break;
            case IOS:
                Optional<IOSVersion> second = Arrays.stream(IOSVersion.values()).filter(version -> version.getVersion().equals(osVersion)).findFirst();
                if(second.isPresent()) {
                    os = second.get();
                }
                break;
        }
        if(os==null) {
            throw new UnsupportedOSException(String.format("OS version %s is not supported on optimus for platform %s",osVersion,platform.getName()));
        }
        return os;
    }

    public boolean isUDIDAvailable(JSONObject testFeed) {
        try{
            JSONObject appiumServerCapabilities = (JSONObject) ((JSONObject) testFeed.get("optimusDesiredCapabilities")).get("appiumServerCapabilities");
            String udid = appiumServerCapabilities.getString("udid");
            return udid==null;
        } catch (Exception e){

        }
        return false;

    }

    public String getUDID(JSONObject testFeed) {
        JSONObject appiumServerCapabilities = (JSONObject) ((JSONObject) testFeed.get("optimusDesiredCapabilities")).get("appiumServerCapabilities");
        String udid = appiumServerCapabilities.getString("udid");
        return udid;
    }
}

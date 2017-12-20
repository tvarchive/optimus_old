package com.testvagrant.optimus.parser;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.testvagrant.commons.entities.DeviceDetails;
import com.testvagrant.commons.exceptions.DeviceEngagedException;
import com.testvagrant.optimus.builder.CapabilitiesBuilder;
import com.testvagrant.optimus.device.DeviceFinder;
import com.testvagrant.optimus.entity.ExecutionDetails;
import com.testvagrant.optimus.entity.Sauce;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.util.HashMap;

import static com.testvagrant.optimus.parser.TestFeedConstants.*;

/**
 * Created by abhishek on 24/05/17.
 */
public class OptimusConfigParser {
    JSONObject jsonObject;
    JsonObject jsonObjectSp;

    public OptimusConfigParser(String appJson) {
        jsonObject = new JSONObject(appJson);
        jsonObjectSp = getObjectFromJson(appJson, JsonObject.class);
    }



    public ExecutionDetails getExecutionDetails() {
        ExecutionDetails execDetails = getObjectFromJson(jsonObjectSp.get(EXEC_DETAILS),ExecutionDetails.class);
        return execDetails;
    }

    public Sauce getSauceLabsDetails() {
        Sauce sauce = new Sauce();
        if (jsonObject.getJSONObject(EXEC_DETAILS).has(SAUCE)) {
            JSONObject sauceJson = jsonObject.getJSONObject(EXEC_DETAILS).getJSONObject(SAUCE);
            sauce.setSauceUserName(sauceJson.getString(SAUCE_USER_NAME));
            sauce.setSauceAccessKey(sauceJson.getString(SAUCE_ACCESS_KEY));
            return sauce;
        }
        return null;
    }

    public boolean isMonitoring() {
       return true;
    }

    public HashMap<String, DesiredCapabilities> mapOwnerToDesiredCapabilities() throws IOException, DeviceEngagedException {
        HashMap<String, DesiredCapabilities> ownerToCapabilitiesMap = new HashMap();

        JSONArray testFeedArray = (JSONArray) jsonObject.get(TEST_FEED);
        for (int testFeedIterator = 0; testFeedIterator < testFeedArray.length(); testFeedIterator++) {

            JSONObject testFeedJSON = (JSONObject) testFeedArray.get(testFeedIterator);
            System.out.println("updated testFeed -- " + testFeedJSON.toString());
            DeviceDetails deviceDetails = new DeviceFinder().getAvailableDeviceAndUpdateToEngaged(testFeedJSON);
            DesiredCapabilities desiredCapabilities = new CapabilitiesBuilder(testFeedJSON, deviceDetails).buildCapabilities();
            ownerToCapabilitiesMap.put((String) testFeedJSON.get(BELONGS_TO), desiredCapabilities);
        }
        return ownerToCapabilitiesMap;
    }

    public boolean isForAndroid() {
        JSONArray testFeedArray = (JSONArray) jsonObject.get(TEST_FEED);
        for (int testFeedIterator = 0; testFeedIterator < testFeedArray.length(); testFeedIterator++) {
            JSONObject testFeedJSON = (JSONObject) testFeedArray.get(testFeedIterator);
            if (testFeedJSON.getJSONObject(OPTIMUS_DESIRED_CAPABILITIES).getJSONObject(APPIUM_SERVER_CAPABILITIES).getString(PLATFORM_NAME)
                    .equalsIgnoreCase("android")) {
                return true;
            }
        }
        return false;
    }

    public boolean isForIos() {
        JSONArray testFeedArray = (JSONArray) jsonObject.get(TEST_FEED);
        for (int testFeedIterator = 0; testFeedIterator < testFeedArray.length(); testFeedIterator++) {
            JSONObject testFeedJSON = (JSONObject) testFeedArray.get(testFeedIterator);
            if (testFeedJSON.getJSONObject(OPTIMUS_DESIRED_CAPABILITIES).getJSONObject(APPIUM_SERVER_CAPABILITIES).getString(PLATFORM_NAME)
                    .equalsIgnoreCase("ios")) {
                return true;
            }
        }
        return false;
    }

    public boolean isForInterApp() {
        JSONArray testFeedArray = (JSONArray) jsonObject.get(TEST_FEED);
        if (testFeedArray.length() > 1)
            return true;
        return false;
    }

    public String getAppBelongingTo(String appConsumer) {
        JSONArray testFeedArray = (JSONArray) jsonObject.get(TEST_FEED);
        for (int testFeedIterator = 0; testFeedIterator < testFeedArray.length(); testFeedIterator++) {
            JSONObject testFeedJSON = (JSONObject) testFeedArray.get(testFeedIterator);
            if (testFeedJSON.getString(BELONGS_TO).equalsIgnoreCase(appConsumer))
                return testFeedJSON.getJSONObject(OPTIMUS_DESIRED_CAPABILITIES).getJSONObject(APPIUM_SERVER_CAPABILITIES).getString(APP);

        }
        throw new RuntimeException("No app found for -- " + appConsumer);
    }


    private <T> T getObjectFromJson(String appJson, Class<T> classOfT) {
        return new Gson().fromJson(appJson, classOfT);
    }

    private <T> T getObjectFromJson(JsonElement jsonElement, Class<T> classOfT) {
        return new Gson().fromJson(jsonElement, classOfT);
    }
}

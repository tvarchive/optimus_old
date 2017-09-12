/*
 * Copyright (c) 2017.  TestVagrant Technologies
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.testvagrant.optimus.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.testvagrant.commons.entities.reportParser.ExecutedScenario;
import com.testvagrant.commons.entities.reportParser.Feature;
import com.testvagrant.commons.entities.reportParser.Step;
import com.testvagrant.optimus.builder.ScenarioBuilder;
import com.testvagrant.optimus.builder.StepBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ReportParser {


    private File reportFolder;

    public ReportParser(File reportFolder) {
        this.reportFolder = reportFolder;
    }

    public List<ExecutedScenario> parse() throws IOException {

        File[] files = reportFolder.listFiles();

        List<ExecutedScenario> scenarios = new ArrayList<>();

        for (File file : files) {
            if(!file.isDirectory()) {
                String fileContent = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                JsonArray featureArray = new JsonParser().parse(fileContent).getAsJsonArray();


                for (JsonElement jsonElement : featureArray) {
                    Feature feature = new Feature(jsonElement.getAsJsonObject());

                    JsonArray scenarioArray = feature.getScenarioArray();
                    List<Step> backgroundStepsList = getBackgroundStepsIfPresent(feature);

                    for (JsonElement element : scenarioArray) {
                        if (!isBackground(element)) {
                            String id = element.getAsJsonObject().get("id").getAsString();
                            JsonArray steps = element.getAsJsonObject().get("steps").getAsJsonArray();
                            List<Step> stepList = new ArrayList<>();
                            stepList.addAll(backgroundStepsList);
                            for (JsonElement step : steps) {
                                stepList.add(getStepDetails(step));
                            }

                            scenarios.add(new ScenarioBuilder()
                                    .withId(id)
                                    .withSteps(stepList)
                                    .withDeviceName(getDeviceName(steps))
                                    .withEmbeddedScreen(getEmbeddedScreenshot(steps))
                                    .build());
                        }
                    }
                }
            }
        }

        printDetails(scenarios);
        return scenarios;
    }

    private String getDeviceName(JsonArray steps) {
        JsonElement lastStep = steps.get(steps.size() - 1);
        JsonObject lastStepObject = lastStep.getAsJsonObject();
        System.out.println("Last Step" + lastStepObject.get("name").getAsString());
        JsonArray outputArray = lastStepObject.get("output").getAsJsonArray();
        return outputArray.get(outputArray.size() - 1).getAsString();
    }

    private byte[] getEmbeddedScreenshot(JsonArray steps) {
        JsonElement lastStep = steps.get(steps.size() - 1);
        if (lastStep.getAsJsonObject().has("embeddings")) {
            JsonArray embeddings = lastStep.getAsJsonObject().get("embeddings").getAsJsonArray();
            String data = embeddings.get(0).getAsJsonObject().get("data").getAsString();
            return Base64.getDecoder().decode(data);
        }
        return new byte[0];
    }


    private boolean isBackground(JsonElement element) {
        return element.getAsJsonObject().get("type").getAsString().equalsIgnoreCase("background");
    }

    private List<Step> getBackgroundStepsIfPresent(Feature feature) {
        List<Step> backgroundStepsList = new ArrayList<>();

        if (feature.hasBackground()) {
            JsonObject firstScenario = feature.getScenarioArray().get(0).getAsJsonObject();
            JsonArray backgroundSteps = firstScenario.getAsJsonObject().get("steps").getAsJsonArray();
            for (JsonElement backgroundStep : backgroundSteps) {
                backgroundStepsList.add(getStepDetails(backgroundStep));
            }
        }
        return backgroundStepsList;
    }


    private void printDetails(List<ExecutedScenario> scenarios) {
        for (ExecutedScenario scenario : scenarios) {
            System.out.println("----------------Scenario--------------");
            System.out.println(scenario.getId() + "-----" + scenario.getDeviceName());

            System.out.println("-----------------Steps-------------------");
            for (Step step : scenario.getSteps()) {
                System.out.println(step.getKeyword() + step.getName() + " ===== " + step.getStatus());
            }
        }
    }

    private Step getStepDetails(JsonElement step) {
        JsonObject stepObject = step.getAsJsonObject();

        String keyword = stepObject.get("keyword").getAsString();
        String stepName = stepObject.get("name").getAsString();
        JsonObject result = stepObject.get("result").getAsJsonObject();
        String status = result.get("status").getAsString();
        String error_message = null;
        if (result.has("error_message")) {
            error_message = result.get("error_message").getAsString();
        }

        return new StepBuilder()
                .withName(stepName)
                .withKeyword(keyword)
                .withStatus(status)
                .withErrorMessage(error_message)
                .build();
    }


}

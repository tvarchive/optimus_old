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

package com.testvagrant.optimus.helpers;

import cucumber.api.Scenario;
import org.apache.commons.io.FilenameUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScenarioHelper {

    private Scenario scenario;
    private Matcher matcher;

    public ScenarioHelper(Scenario scenario) {
        this.scenario = scenario;
        matcher = getMatcher();
    }

    public String getUniqueScenarioName() {
        System.out.println("Scenario id -- " + scenario.getId());
        System.out.println("Scenario Name -- " + scenario.getName());
//        return scenario.getName();
        String scenarioNameString = matcher.group(3);
        if (Character.isDigit(scenarioNameString.charAt(scenarioNameString.length() - 1)) && scenarioNameString.contains(";;")) {
            String outlineCount = scenarioNameString.split(";;")[1];
            return scenarioNameString.split(";;")[0] + "-" + outlineCount;
        }
        return scenarioNameString;
    }

    private Matcher getMatcher() {
        Pattern p = Pattern.compile("((.*?);)(.*)(;;[0-9+])?");
        String id = scenario.getId();
        String[] split = id.split(":");
        String featureName = FilenameUtils.getBaseName(split[0]);
        String lineNumber = split[1];
        String name = scenario.getName();
        String input = featureName + ";" + name + ";;" + lineNumber;
        Matcher matcher = p.matcher(input);

        System.out.println("unique scenario name -- " + input);
        matcher.find();
        return matcher;
    }


    public String getParentFeatureName() {
        return matcher.group(2);
    }
}

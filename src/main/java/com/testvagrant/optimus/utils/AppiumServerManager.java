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

package com.testvagrant.optimus.utils;

import com.testvagrant.optimus.entity.ExecutionDetails;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static io.appium.java_client.service.local.flags.GeneralServerFlag.SESSION_OVERRIDE;

public class AppiumServerManager {


    private ExecutionDetails executionDetails;
    private Optional<Integer> port;

    public AppiumServerManager(ExecutionDetails executionDetails) {
        this.executionDetails = executionDetails;
        port = Optional.empty();
    }


    public AppiumDriverLocalService startAppiumService(String scenarioName, String udid) {
        AppiumDriverLocalService appiumService;
        AppiumServiceBuilder serviceBuilder = new AppiumServiceBuilder();
        serviceBuilder.usingDriverExecutable(new File(executionDetails.getAppium_node_path()))
                .withAppiumJS(new File(executionDetails.getAppium_js_path()))
                .withIPAddress("127.0.0.1")
                .withArgument(SESSION_OVERRIDE)
                .withLogFile(new File(String.format("build/%s.log", scenarioName + "_" + udid)));

        if(port.isPresent()) {
            serviceBuilder.usingPort(port.get());
        } else {
            serviceBuilder.usingAnyFreePort();
        }
        appiumService = serviceBuilder.build();
        appiumService.start();
        await().atMost(5, TimeUnit.SECONDS).until(() -> appiumService.isRunning());
        return appiumService;

    }

    public AppiumServerManager withPort(int port) {
        this.port = Optional.of(port);
        return this;
    }


    private Integer aRandomOpenPortOnAllLocalInterfaces() {
        try (
                ServerSocket socket = new ServerSocket(0);
        ) {
            return socket.getLocalPort();

        } catch (IOException e) {
            throw new RuntimeException("no open ports found for bootstrap");
        }
    }
}

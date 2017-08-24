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

import com.testvagrant.commons.exceptions.OptimusException;
import com.testvagrant.devicemanagement.io.MongoReader;
import com.testvagrant.optimus.device.OptimusTestBase;
import com.testvagrant.optimus.register.DeviceRegistrar;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.when;

public class DeviceHelperTest extends OptimusTestBase {


    @InjectMocks
    MongoReader mongoReader = Mockito.spy(new MongoReader());

    @Mock
    DeviceHelper deviceHelper;

    @Before
    public void setup() {
        new DeviceRegistrar().setUpDevices(deviceMatrix);
        deviceHelper = Mockito.spy(new DeviceHelper(getAppJson("singleApp_Local_Sequential_Android_Emulator.json")));
        when(mongoReader.getAllDevices()).thenReturn(getMockedDevices());
    }

    @Test
    public void shouldBeAbleToReadConnectedDevices() throws OptimusException {
        List<String> udidOfConnectedDevices = deviceHelper.getConnectedDevicesMatchingRunCriteria(mongoReader);
        Assert.assertTrue(udidOfConnectedDevices.size() > 0);
    }
}

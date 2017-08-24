package com.testvagrant.optimus.utils;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by krishnanand on 26/07/17.
 */
public class WDAServerTest {

    @Test
    public void test() throws IOException {
        WDAServerManager wdaServerManager  = new WDAServerManager("40EB53D0-B4AE-44EE-834D-9B543E5D8FDC");
        wdaServerManager.startServer();
    }

    @Test
    public void test1() throws IOException {
        WDAServerManager wdaServerManager  = new WDAServerManager("406E98FB-048D-42D0-8063-08B8256E7F61");
        wdaServerManager.startServer();
    }
}

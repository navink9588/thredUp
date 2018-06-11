package com.thredUp.server;

import com.thredUp.common.Properties;

/**
 * @author Navin Kumar
 * @date 6/11/18
 */
public class ServerProperties extends Properties {
    private static final String TIMEOUT_PROP = "inactivity.timeout.sec";
    private final Long timeoutMs;

    private static final String SIMULATED_DEVICE_COUNT = "simulated.device.count";
    private final int deviceCount;

    public ServerProperties() {
        super();
        timeoutMs = getProperty(TIMEOUT_PROP, 10L) * 1000;
        deviceCount = getProperty(SIMULATED_DEVICE_COUNT, 100);
    }

    public Long getTimeoutMs() {
        return timeoutMs;
    }

    public int getDeviceCount() {
        return deviceCount;
    }
}

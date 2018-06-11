package com.thredUp.client;

import com.thredUp.common.Properties;

/**
 * @author Navin Kumar
 * @date 6/11/18
 */
public class ClientProperties extends Properties {
    private static final String SERVER_HOST_PROP = "server.host";
    private final String serverHost;

    public ClientProperties() {
        super();
        serverHost = System.getProperty(SERVER_HOST_PROP, "localhost");
    }

    public String getServerHost() {
        return serverHost;
    }
}

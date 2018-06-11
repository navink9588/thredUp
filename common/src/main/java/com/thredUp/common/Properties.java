package com.thredUp.common;

import org.apache.log4j.Logger;

/**
 * @author Navin Kumar
 * @date 6/11/18
 */
public class Properties {
    private static Logger LOG = Logger.getLogger(Properties.class);

    private static final String SERVER_PORT = "server.port";
    private final int port;

    public Properties() {
        port = getProperty(SERVER_PORT, 7000);
    }

    public int getPort() {
        return port;
    }

    protected <T> T getProperty(String key, T def) {
        String val = System.getProperty(key);
        if(val != null && !val.isEmpty()) {
            try {
                if(def instanceof Long) {
                    return (T) Long.valueOf(val);
                } else if (def instanceof Integer) {
                    return (T) Integer.valueOf(val);
                }
            } catch (NumberFormatException ignore) {
                LOG.error(String.format("Invalid value '%s' for property '%s'. "
                    + "Returning default value '%s'", val, key, def));
            }
        }
        return def;
    }
}

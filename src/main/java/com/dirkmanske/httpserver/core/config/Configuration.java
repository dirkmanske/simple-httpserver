package com.dirkmanske.httpserver.core.config;

import java.io.IOException;
import java.util.Properties;

/**
 * Provides the configured values for port, number of worker threads etc.
 *
 * @author dirk.manske
 */
public final class Configuration {

    public static final int AVAILABLE_PROCS = Runtime.getRuntime().availableProcessors() * 2;

    public static final String LISTENER_PORT = "listener.port";

    public static final String LISTENER_THREADS = "listener.threads";

    public static final String WORKER_THREADS = "worker.threads";

    public static final String DOCROOT = "docroot";

    private static final String CONFIG_FILE = "config.properties";

    private static final Configuration INSTANCE = new Configuration();

    private final Properties props = new Properties();

    private Configuration() {
        loadProperties();
    }

    public static Configuration getInstance() {
        return INSTANCE;
    }

    private void loadProperties() {
        try {
            props.load(Configuration.class.getResourceAsStream("/" + CONFIG_FILE));
        } catch (IOException ex) {
            throw new RuntimeException("Could not load properties", ex);
        }
    }

    public String getString(final String key) {
        return props.getProperty(key);
    }

    public Integer getInt(final String key) {
        return Integer.valueOf(props.getProperty(key));
    }

}

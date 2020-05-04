package com.tibiawiki.domain.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesUtil.class);

    private PropertiesUtil() {
        // don't instantiate this class, it has only static members
    }

    public static String getUsername() {
        return getProperty("username");
    }

    public static String getPassword() {
        return getProperty("password");
    }

    private static String getProperty(String propertyName) {
        try {
            Properties props = new Properties();
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("credentials.properties");

            if (is != null) {
                props.load(is);
                return props.getProperty(propertyName);
            } else {
                LOG.warn("Could not read requested propertyName {} from file 'credentials.properties'.", propertyName);
                return null;
            }
        } catch (Exception ex) {
            LOG.error(String.valueOf(ex));
        }
        return null;
    }
}

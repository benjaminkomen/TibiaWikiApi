package com.tibiawiki.domain.utils;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesUtil.class);

    private PropertiesUtil() {
        // don't instantiate this class, it has only static members
    }

    @Nullable
    public static String getUsername() {
        return getProperty("username");
    }

    @Nullable
    public static String getPassword() {
        return getProperty("password");
    }

    @Nullable
    private static String getProperty(String propertyName) {
        String output = null;
        try {
            Properties props = new Properties();
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("credentials.properties");
            props.load(is);
            return props.getProperty(propertyName);
        } catch (Exception ex) {
            LOG.error(String.valueOf(ex));
        }
        return output;
    }
}

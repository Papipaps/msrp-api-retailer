package fr.mspr.retailer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SecretPropertiesUtil {

    public static String getProperties(String property) {
        String resourceName = "secret.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            props.load(resourceStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return props.getProperty(property);
    }
}

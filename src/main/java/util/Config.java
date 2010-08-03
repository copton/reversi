package util;

import java.util.Properties;
import java.io.InputStream;

public class Config {
    public static String get(String key) 
    {
        return getInstance().getConfig(key);
    }

    private String getConfig(String key)
    {
        String value = properties.getProperty(key);
        if (value == null) {
            System.err.println("could not find property '" + key + "'.");
        }
        return value;
    }
    
    private static Config instance = null;
    private Properties properties;

    private static synchronized Config getInstance()
    {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private Config() { 
        properties = new Properties();

        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("reversi.properties");
            if (is == null) {
                System.err.println("Could not find reversi.properties in classpath");
            } else {
                properties.load(is);
            }
        } catch (java.io.IOException e) {
            System.err.println("Failed to load Reversi properties: " + e.getMessage());
        }
    }
}

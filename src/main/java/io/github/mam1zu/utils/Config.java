package io.github.mam1zu.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
    private static final String CONFIG_PATH = "config/config.properties";
    Properties prop;
    String proppass = "config";
    private Config() {
        prop = new Properties();
        try {
            prop.load(Files.newBufferedReader(Paths.get(CONFIG_PATH), StandardCharsets.UTF_8));

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty() {

        return getProperty();
    }


}

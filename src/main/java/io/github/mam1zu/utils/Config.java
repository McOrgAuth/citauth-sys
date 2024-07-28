package io.github.mam1zu.utils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

public class Config {
    private static final String CONFIG_PATH = "src/main/java/io/github/mam1zu/config/config.properties";
    Properties prop;

    //MySQL configs
    private String host;
    private String db;
    private String user;
    private String password;
    private String port;

    public Config() {

        Properties prop = new Properties();
        File file = new File(CONFIG_PATH);

        if(!file.exists()) {
            System.err.println("Config file not found");
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            prop.load(fis);
        } catch (FileNotFoundException e) {
            System.err.println("Config file not found!");
            e.printStackTrace();
            return;
        } catch (Exception e) {
            System.out.println("You what");
            e.printStackTrace();
            return;
        }

        this.prop = prop;

    }

    public void loadConfig() {

        if(this.prop == null) {
            return;
        }

        this.host = this.prop.getProperty("mysqlhost");
        this.db = this.prop.getProperty("mysqldb");
        this.user = this.prop.getProperty("mysqluser");
        this.password = this.prop.getProperty("mysqlpassword");
        this.port = this.prop.getProperty("mysqlport");

    }

    public HashMap<String, String> getMysqlConfig() {

        HashMap<String, String> configmap = new HashMap<>();

        configmap.put("host", this.host);
        configmap.put("db", this.db);
        configmap.put("user", this.user);
        configmap.put("password", this.password);
        configmap.put("port", this.port);

        return configmap;

    }




}

package com.itt.tds.cfg;

import java.util.*;
import java.util.logging.Logger;
import java.io.*;
import com.google.gson.*;
import com.google.gson.stream.*;

public class TDSConfiguration {

    private static Logger log = Logger.getLogger(TDSConfiguration.class.getName());

    /*
        Config Items
    */

    private static String pathSeparator         = "";
    private static String configFile            = "./com/itt/tds/cfg/TDSConfiguration.json";
    private static String dBConnectionString    = "";
    private static String dBConnectionUser      = "";
    private static String dBConnectionPassword  = "";
    private static String protocolFormat        = "";
    private static String protocolVersion       = "";
    private static int    coordinatorPort       = -1;
    private static String coordinatorIp         = "";
    private static String coordinatorTaskDir    = "";
    private static int    nodePort              = -1;

    /*
        JSON Config Structure
    */

    private class ConfigJSON {

        public String   dBConnectionString;
        public String   dBConnectionUser;
        public String   dBConnectionPassword;
        public String   protocolFormat;
        public String   protocolVersion;
        public int      coordinatorPort;
        public String   coordinatorIp;
        public String   coordinatorTaskDir;
        public int      nodePort;
    }

    /*
        Configuration as Singleton
    */

    private static TDSConfiguration singleton = new TDSConfiguration();

    public static synchronized TDSConfiguration getInstance() {
        return singleton;
    }

    private TDSConfiguration() {

        try {

            determinePathSeparator();

            Gson gson         = new Gson();
            JsonReader reader = new JsonReader(new FileReader(new File(configFile)));
            ConfigJSON config = gson.fromJson(reader, ConfigJSON.class);

            dBConnectionString      = config.dBConnectionString;
            dBConnectionUser        = config.dBConnectionUser;
            dBConnectionPassword    = config.dBConnectionPassword;
            protocolFormat          = config.protocolFormat;
            protocolVersion         = config.protocolVersion;
            coordinatorPort         = config.coordinatorPort;
            coordinatorIp           = config.coordinatorIp;
            coordinatorTaskDir      = config.coordinatorTaskDir;
            nodePort                = config.nodePort;

        } catch (FileNotFoundException fnfException) {

            System.out.println("Config JSON file could not be found - check working directory");
            System.out.println(fnfException.getMessage());
            System.exit(0);
        }
    }

    private void determinePathSeparator(){

        String os = System.getProperty("os.name");

        if(os.equals("Windows 10"))
            pathSeparator = "\\";
        else if (os.equals("Mac OS X"))
            pathSeparator = "/";
        else
            pathSeparator = "/";
    }

    /*
        Methods
    */

    public static String getPathSeparator() {
        return pathSeparator;
    }

    public static String getDBConnectionString() {
        return dBConnectionString;
    }

    public static String getDBConnectionUser() {
        return dBConnectionUser;
    }

    public static String getDBConnectionPassword() {
        return dBConnectionPassword;
    }

    public static String getProtocolFormat(){
        return protocolFormat;
    }

    public static String getProtocolVersion(){
        return protocolVersion;
    }

    public static int getCoordinatorPort(){
        return coordinatorPort;
    }

    public static String getCoordinatorIp(){
        return coordinatorIp;
    }

    public static String getCoordinatorTaskDir(){
        return coordinatorTaskDir;
    }

    public static int getNodePort(){
        return nodePort;
    }
}
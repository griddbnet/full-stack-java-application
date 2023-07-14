package com.fixstars.agent;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.ArrayList;

class GridLogAgent {

    String hostname;
    String griddbURL;

    public GridLogAgent (String hostname, String griddbURL) {
        this.hostname = hostname;
        this.griddbURL = griddbURL;
    }

    public static ArrayList<HashMap<String, String>> getLogConf (String path) {
       // System.out.println("Reading log config file " + path);
        InputStream input;
        Properties props  = new Properties();
        ArrayList<HashMap<String, String>> logConf = new ArrayList<HashMap<String, String>>();
        

        try {
			input = new FileInputStream(path);
			props.load(input);
		} catch (Exception e) {
			System.out.println("COULD NOT LOAD source.properties");
		}

        HashMap<String, String> map1 = new HashMap<>();
        map1.put("path", props.getProperty("path1"));
        map1.put("type", props.getProperty("type1"));
        map1.put("poll_interval", props.getProperty("poll_interval"));

        HashMap<String, String> map2 = new HashMap<>();
        map2.put("path", props.getProperty("path2"));
        map2.put("type", props.getProperty("type2"));
        map2.put("poll_interval", props.getProperty("poll_interval"));

        logConf.add(map1);
        logConf.add(map2);
        return logConf;
    }

    
    public static HashMap<String, String> getGridDBConf (String path) {
        InputStream input;
        Properties props  = new Properties();
        try {
			input = new FileInputStream(path);
			props.load(input);
		} catch (Exception e) {
			System.out.println("COULD NOT LOAD source.properties");
		}
        HashMap<String, String> griddbConf = new HashMap<>();
        griddbConf.put("url", props.getProperty("url"));
        griddbConf.put("username", props.getProperty("admin"));
        return griddbConf;
    }

    public static void main(String[] args) {

        ArrayList<HashMap<String, String>> logConfs = getLogConf("./conf/logs.properties");
        HashMap<String, String> griddbConf = getGridDBConf("./conf/griddb.properties");

        String url = griddbConf.get("url");
        GridLogAgent logAgent = new GridLogAgent("host1", url);

        for(HashMap<String, String> logConf : logConfs) {
            LogReader logReader = new LogReader(logConf, logAgent.hostname, logAgent.griddbURL);
            logReader.start();
        }

        for(HashMap<String, String> logConf : logConfs) {
            LogReader logReader = new LogReader(logConf, logAgent.hostname, logAgent.griddbURL);
            try {
                logReader.join();
            } catch(InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
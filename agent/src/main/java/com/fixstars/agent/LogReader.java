package com.fixstars.agent;

import java.io.*;
import java.util.*; 

// Date ts, 
// String hostname, 
// String logtype, 
// String value, 
// String (file)path

class LogReader extends Thread {

    HashMap<String, String> logConf;
    String hostname;
    String griddbURL;
    static int POLL_INTERVAL;

    public LogReader (HashMap<String, String> logConf, String hostname, String griddbURL) {
        this.logConf = logConf;
        this.hostname = hostname;
        this.griddbURL = griddbURL;
        int poll_int = Integer.parseInt(logConf.get("poll_interval"));
        this.POLL_INTERVAL = poll_int;
    }
    
    private static void monitorFile(File file, String hostname, String logtype, String path,  String griddbURL ) throws IOException {
        FileReader reader = new FileReader(file);
        BufferedReader buffered = new BufferedReader(reader);
        GridDBWriter gWriter = new GridDBWriter(hostname, logtype, path, griddbURL);
        gWriter.createContainer();
        StringBuilder str = new StringBuilder(); 
        str.append("[");
        try {
            while(true) {
                String line = buffered.readLine();              
                if(line == null) {
                    // end of file, start polling
                    Thread.sleep(POLL_INTERVAL);
                    str.setLength(str.length() - 1); //remove last comma
                    str.append("]");
                    if (str.length() > 1) { // will continue to try to write ']' without this check
                        gWriter.writeLog(str.toString());
                    }
                    str.setLength(0);
                    str.append("[");
                } else {
                    Date date = new Date();
                    String rawLogString = gWriter.putRowString(date.toString(), hostname, logtype,line, path);
                    str.append(rawLogString);
                    str.append(",");
                }
            }
        } catch(InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        String filePath = logConf.get("path");
        String logtype = logConf.get("type");
        File f = new File(filePath);
        try {
            monitorFile(f, hostname, logtype, filePath, griddbURL);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
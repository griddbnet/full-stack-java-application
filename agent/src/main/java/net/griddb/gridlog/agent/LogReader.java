package net.griddb.gridlog.agent;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.*;

import net.griddb.gridlog.agent.ConfigParser.LogsConfig; 


// Date ts, 
// String hostname, 
// String logtype, 
// String value, 
// String (file)path

class LogReader extends Thread {

    LogsConfig logConf;
    String hostname;
    String griddbURL;

    public LogReader (LogsConfig logConf, String hostname, String griddbURL) {
        this.logConf = logConf;
        this.hostname = hostname;
        this.griddbURL = griddbURL;
    }
    
    private static void monitorFile(File file, String hostname, String logtype, long interval, String path,  String griddbURL ) throws IOException {
        FileReader reader = new FileReader(file);
        BufferedReader buffered = new BufferedReader(reader);
        GridDBWriter gWriter = new GridDBWriter(hostname, logtype, path, griddbURL);
        gWriter.createContainer(true); //creates container for container info (if doesn't exist)
        gWriter.createContainer(false); // creates container for specific logs

        // string for raw log container
        StringBuilder str = new StringBuilder();

        // string for coninfo container SCHEMA=name,last_write
        StringBuilder strConInfo = new StringBuilder(); 

        str.append("[");
        Instant utcTime = Instant.now();
        Date date = Date.from(utcTime); 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String containerInfoString = "";
        try {
            while(true) {
                String line = buffered.readLine();              
                if(line == null) {
                    // end of file, start polling
                    Thread.sleep(interval);
                    str.setLength(str.length() - 1); //remove last comma
                    str.append("]");

                    if (str.length() > 1) { // will continue to try to write ']' without this check
                        gWriter.writeLog(str.toString(), "rawlog");
                        
                        strConInfo.append("[");
                        containerInfoString = gWriter.writeToContainerInfo(sdf.format(date), hostname, logtype);
                        strConInfo.append(containerInfoString);
                        strConInfo.append("]");
                        
                        //
                        gWriter.writeLog(strConInfo.toString(), "RAWLOG_writes");
                    }
                    str.setLength(0);
                    strConInfo.setLength(0);
                    str.append("[");

                } else {
                    utcTime = Instant.now();
                    date = Date.from(utcTime); 
                    String rawLogString = gWriter.putRowString(sdf.format(date), hostname, logtype,line, path);

                    str.append(rawLogString);
                    str.append(",");
                }
            }
        } catch(InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        String filePath = logConf.path.toString();
        String logtype = logConf.type.toString();
        long interval = logConf.interval;
        File f = new File(filePath);
        try {
            monitorFile(f, hostname, logtype, interval, filePath, griddbURL);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
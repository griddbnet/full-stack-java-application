package net.griddb.gridlog.logviewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.List;
import java.util.HashMap;

import java.net.URLEncoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class GridDB {

    public Connection con;

    public GridDB() {
        try {
            // String serverName = "griddb-server";
            // String encodeServerName = URLEncoder.encode(serverName, "UTF-8");
            String notificationMember = "griddb-server:20001";
            String clusterName = "myCluster";
            String databaseName = "public";
            String username = "admin";
            String password = "admin";
            String encodeClusterName = URLEncoder.encode(clusterName, "UTF-8");
            String encodeDatabaseName = URLEncoder.encode(databaseName, "UTF-8");
            String jdbcUrl = "jdbc:gs://" + notificationMember + "/" + encodeClusterName + "/" + encodeDatabaseName;

            Properties prop = new Properties();
            prop.setProperty("user", username);
            prop.setProperty("password", password);

            System.out.println(jdbcUrl);

            con = DriverManager.getConnection(jdbcUrl, prop);

        } catch (Exception e) {
            System.out.println("Could not connect to GridDB via JDBC, exiting.");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public List<String> getContainerNames() {
        List<String> listOfContainerNames = new ArrayList<String>();
        try {
            Statement stmt = con.createStatement(); 

            ResultSet rs = stmt.executeQuery("SELECT DISTINCT name from RAWLOG_writes;");

            while (rs.next()) {
                String name = rs.getString(1);
                listOfContainerNames.add(name);

                // System.out.println("SQL Row temp: " + ts + " " + hostname + log);
            }

        } catch (Exception e) {
            System.out.println("Error with getting RAWLOG_writes: getContainerNames()");
            e.printStackTrace();
        }

        return listOfContainerNames;
    }

    public List<String> getContainerNamesWithParameters(String hostname, String logtype) {
        List<String> listOfContainers = new ArrayList<String>();
        String queryStr = "SELECT name FROM RAWLOG_writes where name LIKE ?";
        
        try {
            PreparedStatement stmt = con.prepareStatement(queryStr);
            if (hostname != "none" && logtype.equals("none")) { // User selects Hostname, but keeps logtype as none
                //SELECT name FROM RAWLOG_writes where name LIKE 'RAWLOG_plex%';
                queryStr = "SELECT name FROM RAWLOG_writes where name LIKE ?;";
                stmt = con.prepareStatement(queryStr);
                String str = "RAWLOG_" + hostname + "%";
                stmt.setString(1, str);

            } else if (hostname.equals("none") && logtype != "none") { // User selects Logtype, but keeps hostname as none
                //SELECT name FROM RAWLOG_writes where name LIKE '%tests';
                queryStr = "SELECT name FROM RAWLOG_writes where name LIKE ?";
                stmt = con.prepareStatement(queryStr);
                String str = "%" + logtype;
                stmt.setString(1, str);
            }
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString(1);
                listOfContainers.add(name);
            }

        } catch (Exception e) {
            System.out.println("Error with getting RAWLOG_writes: getContainerNamesWithParameters()");
            e.printStackTrace();
        }
        return listOfContainers;
    }

    public HashMap<String, List<Log>> queryAllContainersFromUserInput(
            List<String> listOfContainers,
            String start,
            String end) {

        HashMap<String, List<Log>> retval = new HashMap<String, List<Log>>();

        try {
            for (String container : listOfContainers) {
                List<Log> listOfLogs = new ArrayList<Log>();
                String cont = container.replaceAll("RAWLOG", "LOG");
                String queryStr = ("SELECT * FROM " + cont + "  WHERE timestamp > TO_TIMESTAMP_MS(?) AND timestamp < TO_TIMESTAMP_MS(?);");

                PreparedStatement stmt = con.prepareStatement(queryStr);
                long startMili = Long.parseLong(start);
                long endMili = Long.parseLong(end);
                stmt.setLong(1, startMili);
                stmt.setLong(2, endMili);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Date ts = rs.getTimestamp(1);
                    String hn = rs.getString(2);
                    String log = rs.getString(3);
                    Log temp = new Log();
                    temp.ts = ts;
                    temp.hostname = hn;
                    temp.log = log;
                    listOfLogs.add(temp);

                    retval.put(cont, listOfLogs);
                }
            }

        } catch (Exception e) {
            System.out.println("Error with getting RAWLOG_writes: queryAllContainersFromUserInput()");
            e.printStackTrace();
        }

        return retval;
    }

}

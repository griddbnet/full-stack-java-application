package com.fixstars.agent;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class GridDBWriter {

    String logtype;
    String hostname;
    String griddbURL;
    String path;

    public static final MediaType JSON = MediaType.get("application/json");

    public GridDBWriter (String hostname, String logtype, String path, String griddbURL ) {
        this.logtype = logtype;
        this.hostname = hostname;
        this.griddbURL = griddbURL;
        this.path = path;
    }

    public static String getContainerName (String hostname, String logtype) {
        return "RAWLOG_" + hostname + "_" +logtype;
    }

    public String putRowString(String ts, String hostname, String logtype, String value, String path ) {
    return "[" 
        + "\"" + ts + "\"" + ","
        + "\"" + hostname + "\"" + ","
        + "\"" + logtype + "\"" + ","
        + "\"" + value + "\"" + ","
        + "\"" + path + "\"" 
        + "]";
    }

    static String createContainerJSON(String hostname, String logtype) {
        String cn = getContainerName(hostname, logtype);
        System.out.println("Creating container: " + cn);
    return "{\"container_name\": \"" + cn + "\","
        + "\"container_type\":\"COLLECTION\","
        + "\"rowkey\":false,"
        + "\"columns\":["
        + "{\"name\":\"ts\",\"type\":\"STRING\"},{\"name\":\"hostname\",\"type\":\"STRING\"},{\"name\":\"logtype\",\"type\":\"STRING\"},{\"name\":\"value\",\"type\":\"STRING\"},{\"name\":\"path\",\"type\":\"STRING\"}"
        + "]}";
  } 

    public void createContainer () {

        OkHttpClient client = new OkHttpClient();
        String row = createContainerJSON(hostname, logtype);

        RequestBody body = RequestBody.create(row, JSON);
        Request request = new Request.Builder()
            .url(griddbURL + "/griddb/v2/myCluster/dbs/public/containers/")
            .method("POST", body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
            .build();
        try (Response response = client.newCall(request).execute()) {
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeLog (String value) {
        OkHttpClient client = new OkHttpClient();

        System.out.println("Writing to GriDDB: " + value);

        RequestBody body = RequestBody.create(value, JSON);
        String cn = getContainerName(hostname, logtype);
        Request request = new Request.Builder()
            .url(griddbURL + "/griddb/v2/myCluster/dbs/public/containers/" + cn + "/rows")
            .method("PUT", body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
            .build();
        try (Response response = client.newCall(request).execute()) {
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
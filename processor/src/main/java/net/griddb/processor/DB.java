package net.griddb.gridlog.processor;
import com.toshiba.mwcloud.gs.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.time.Instant;

class DB {

    public GridStore store;
    Instant lastPoll;
    public DB ()  throws GSException{

        try {
            Properties props = new Properties();
            props.setProperty("notificationMember", "griddb-server:10001");
            props.setProperty("clusterName", "myCluster");
            props.setProperty("user", "admin"); 
            props.setProperty("password", "admin");
            store = GridStoreFactory.getInstance().getGridStore(props);
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    public void setOffset(String containerName, Date offset) throws GSException {
        System.out.println("Setting "+containerName+" offset to "+offset);
        Collection<String, RawLogInfo> col = store.putCollection("RAWLOG_reads", RawLogInfo.class);
        RawLogInfo info = new RawLogInfo();
        info.name = containerName;
        info.timestamp = offset;
        col.setAutoCommit(false);
        col.put(info);
        col.commit();
        
    }
    public ArrayList<String> getRawLogContainers() throws GSException {
        ArrayList<String> retval = new ArrayList();
        Collection<String, RawLogInfo> col = store.getCollection("RAWLOG_writes", RawLogInfo.class);

        Query<RawLogInfo> query;

        if (lastPoll == null)
            query = col.query("select *");
        else {
            System.out.println("select * where timestamp >= TO_TIMESTAMP_MS("+lastPoll+")");
            query = col.query("select * where timestamp >= TO_TIMESTAMP_MS("+lastPoll.toEpochMilli()+")");
        }
        RowSet<RawLogInfo> rs = query.fetch(false);
        while (rs.hasNext()) {
            retval.add(rs.next().name);
        }
        if(retval.size() > 0)
            lastPoll = Instant.now();
        return retval; 

    }
    public ArrayList<RawLog> getNewLogs(String containerName, Date after) throws GSException {


        Collection<String, RawLogInfo> rawLogInfo = store.putCollection("RAWLOG_reads", RawLogInfo.class);
        Query<RawLogInfo> infoQuery = rawLogInfo.query("select * where name=\'"+containerName+"\'");
        RowSet<RawLogInfo> infoRs = infoQuery.fetch(false);

        RawLogInfo logInfo = null;
        if(infoRs.hasNext())
            logInfo = infoRs.next();

        ArrayList<RawLog> retval = new ArrayList();
        Collection<String, RawLog> rawLogCol = store.getCollection(containerName, RawLog.class);

        Query<RawLog> query;
        if (logInfo == null)
            query = rawLogCol.query("select *");
        else {
            System.out.println("select * where ts > TO_TIMESTAMP_MS("+logInfo.timestamp.getTime()+")");
            query = rawLogCol.query("select * where ts > TO_TIMESTAMP_MS("+logInfo.timestamp.getTime()+")");
        }

        RowSet<RawLog> rs = query.fetch(false);

        while (rs.hasNext()) {
            retval.add(rs.next());
        }
        if (retval.size() > 0)
            setOffset(containerName, retval.get(retval.size()-1).ts);
        return retval;  
        
    }
}

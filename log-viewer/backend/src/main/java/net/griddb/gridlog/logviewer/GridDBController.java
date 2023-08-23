package net.griddb.gridlog.logviewer;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.sql.ResultSet;

import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@CrossOrigin(origins = { "*" }, maxAge = 4800, allowCredentials = "false")
@RestController
public class GridDBController {

    GridDB gridDB;

    public GridDBController() {
        super();
        gridDB = new GridDB();
    }

    @RequestMapping(value = "/getContainers", method = RequestMethod.GET)
    public ResponseEntity<?> getContainerNames() {
        List<String> listOfNames = gridDB.getContainerNames();
        System.out.println("list of names: " + listOfNames);
        return ResponseEntity.ok(listOfNames);
    }

    @RequestMapping(value = "/containersWithParameters", method = RequestMethod.GET)
    public ResponseEntity<?> containersWithParameters(
            @RequestParam String hostname,
            @RequestParam("logtype") String logtype,
            @RequestParam("start") String start,
            @RequestParam("end") String end) {

        HashMap<String, List<Log>> retval = new HashMap<String, List<Log>>();

        if (!hostname.equals("none") && !logtype.equals("none")) {
            List<String> listofContainers = new ArrayList<String>();
            listofContainers.add("RAWLOG_" + hostname + "_" + logtype);
            retval = gridDB.queryAllContainersFromUserInput(listofContainers, start, end);
        } else {
            List<String> listofContainers = gridDB.getContainerNamesWithParameters(hostname, logtype);
            System.out.println("list of containers else ");
            retval = gridDB.queryAllContainersFromUserInput(listofContainers, start, end);
        }

        return ResponseEntity.ok(retval);
    }

}

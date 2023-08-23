package net.griddb.gridlog.agent;

import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.io.FileReader;

class ConfigParser {

    class LogsConfig {
        String type;
        String path;
        long interval;
    };

    HashMap<String, LogsConfig> rules;

    public HashMap<String, LogsConfig> parseConfig (String filename) {
        JSONObject root = new JSONObject();
        rules = new HashMap();
        try {
            root  = (JSONObject) JSONValue.parse(new FileReader(filename));
        } catch (Exception e) {
            System.err.println("couldnt parse "+filename);
            e.printStackTrace();
        }

        JSONArray jsonRules = (JSONArray) root.get("logs");

        for (Object jsonRule_ : jsonRules) {
            JSONObject jsonRule = (JSONObject) jsonRule_;
            LogsConfig rule = new LogsConfig();
            rule.type = (String)jsonRule.get("type");
            rule.path = (String)jsonRule.get("path");
            rule.interval = (long)jsonRule.get("interval");
            rules.put(rule.type, rule);
        }
         
        System.out.println(rules);
        return rules;
    }
    public static void main(String args[]) {
    }
}


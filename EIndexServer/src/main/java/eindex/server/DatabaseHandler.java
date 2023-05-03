/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eindex.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Jovan
 */
public class DatabaseHandler {
    final private static String USERS_FILENAME = "users.txt";
    final private static String USERS_INDEX_FILENAME = "users_index.txt";
    
    public DatabaseHandler() {}
    
    static private BufferedReader createUsersBr() throws FileNotFoundException {
        return new BufferedReader(new FileReader("./data/" + USERS_FILENAME));
    }
    
    public Collection<User> readAllUsers() throws IOException {
        BufferedReader br = createUsersBr();
        Collection<User> users = new ArrayList<>();
        String line;
        while (true) {
            try {
                line = br.readLine();
            } catch (IOException ex) {
                br.close();
                throw ex;
            }
            if (line == null) {
                break;
            }
            String[] userInfo = line.split(";");
            if (userInfo.length == 3) {
                users.add(new User(userInfo[0], userInfo[1], userInfo[2]));
            } else {
                br.close();
                throw new IOException("There is missing/too much user info");
            }
        }
        return users;
    }
    
    public User readUserPer(String data, int data_index) throws IOException {
        BufferedReader br = createUsersBr();
        String line;
        while (true) {
            try {
                line = br.readLine();
            } catch (IOException ex) {
                br.close();
                throw ex;
            }
            if (line == null) {
                break;
            }
            String[] userInfo = line.split(";");
            if (userInfo.length == 3) {
                if (userInfo[data_index].contentEquals(data)) {
                    return new User(userInfo[0], userInfo[1], userInfo[2]);
                }
            } else {
                br.close();
                throw new IOException("There is missing/too much user info");
            }
        }
        return null;
    }
    
    static private FileReader createIndexFr() throws FileNotFoundException {
        return new FileReader("./data/" + USERS_INDEX_FILENAME);
    }
    
    public JSONArray readAllUserIndex() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONArray out;
        try (FileReader fr = createIndexFr()) {
            out = (JSONArray)parser.parse(fr);
        }
        return out;
    }
    
    public JSONObject readUserIndexPer(String value, String key) throws IOException, ParseException {
        JSONArray array = readAllUserIndex();
        for (Object obj : array) {
            JSONObject jObj = (JSONObject)obj;
            if (jObj.get(key).toString().equalsIgnoreCase(value)) {
                return jObj;
            }
        }
        return new JSONObject();
    }
    
    public void updateUserIndexSubject(JSONObject subject, String username) throws IOException, ParseException {
        JSONArray array = readAllUserIndex();
        JSONObject user = null;
        for (Object obj : array) {
            JSONObject jObj = (JSONObject)obj;
            if (jObj.get("username").toString().equalsIgnoreCase(username)) {
                user = jObj;
                break;
            }
        }
        if (user == null) {
            // should not happen
            throw new IOException("There is missing user " + username + " from eIndex database");
        }
        String subjectStr = subject.get("subject").toString();
        JSONObject targetSubject = null;
        for (Object obj : (JSONArray)user.get("subjects")) {
            JSONObject jObj = (JSONObject)obj;
            if (jObj.get("subject").toString().equalsIgnoreCase(subjectStr)) {
                targetSubject = jObj;
                break;
            }
        }
        if (targetSubject == null) {
            // should not happen
            throw new IOException("There is missing subject " + subjectStr + " for user " + username + " from eIndex database");
        }
        targetSubject.put("T1", subject.get("T1"));
        targetSubject.put("T2", subject.get("T2"));
        targetSubject.put("Z1", subject.get("Z1"));
        targetSubject.put("Z2", subject.get("Z2"));
        
        //Write JSON file
        try (FileWriter file = new FileWriter("./data/" + USERS_INDEX_FILENAME)) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(array.toJSONString()); 
            file.flush();
        } catch (IOException e) {
            // just disclaimer for exception
            throw e;
        }
    }
}

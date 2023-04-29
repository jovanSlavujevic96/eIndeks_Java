/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eindex.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
        return (JSONArray)parser.parse(createIndexFr());
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
}

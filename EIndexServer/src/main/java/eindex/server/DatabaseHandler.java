package eindex.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
    // filenames
    final private static String USERS_FILENAME = "users.txt";
    final private static String USERS_INDEX_FILENAME = "users_index.txt";
    
    public DatabaseHandler() {}

    // read all users from database
    public Collection<User> readAllUsers() throws IOException {
        FileReader fr = new FileReader("./data/" + USERS_FILENAME);
        BufferedReader br = new BufferedReader(fr);

        Collection<User> users = new ArrayList<>();
        String line;
        while (true) {
            try {
                line = br.readLine();
            } catch (IOException ex) {
                fr.close();
                br.close();
                throw ex;
            }

            if (line == null) {
                break; // EOL
            } else if (line.contentEquals("")) {
                continue; // empty line due to new line addition
            }

            String[] userInfo = line.split(";");
            if (userInfo.length == 3) {
                users.add(new User(userInfo[0], userInfo[1], userInfo[2]));
            } else {
                fr.close();
                br.close();
                throw new IOException("There is missing/too much user info");
            }
        }

        fr.close();
        br.close();
        return users;
    }

    // read desired user from database
    public User readUserPer(String data, int data_index) throws IOException {
        FileReader fr = new FileReader("./data/" + USERS_FILENAME);
        BufferedReader br = new BufferedReader(fr);

        String line;
        while (true) {
            try {
                line = br.readLine();
            } catch (IOException ex) {
                fr.close();
                br.close();
                throw ex;
            }

            if (line == null) {
                break; // EOL
            } else if (line.contentEquals("")) {
                continue; // empty line due to new line addition
            }

            String[] userInfo = line.split(";");
            if (userInfo.length == 3) {
                if (userInfo[data_index].contentEquals(data)) {
                    fr.close();
                    br.close();
                    return new User(userInfo[0], userInfo[1], userInfo[2]);
                }
            } else {
                fr.close();
                br.close();
                throw new IOException("There is missing/too much user info");
            }
        }

        fr.close();
        br.close();
        return null;
    }

    // write (append) user to database
    public void writeUser(User user) throws IOException {
        FileWriter fw = new FileWriter("./data/" + USERS_FILENAME, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("\n" + user.toString());
        bw.close();
    }

    // read all users from extended database
    public JSONArray readAllUserIndex() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONArray out;
        try (FileReader fr = new FileReader("./data/" + USERS_INDEX_FILENAME)) {
            out = (JSONArray)parser.parse(fr);
        }
        return out;
    }

    // read desired user from extended database 
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

    // update user's subject within extended database
    public void updateUserIndexSubject(JSONObject subject, String username) throws IOException, ParseException {
        JSONArray array = readAllUserIndex();
        JSONObject user = null;

        // iterate through all users in order to find desired user
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

        // iterate through all student's subjects in order to find desired subject
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

        // update subject's categories
        targetSubject.put("T1", subject.get("T1"));
        targetSubject.put("T2", subject.get("T2"));
        targetSubject.put("Z1", subject.get("Z1"));
        targetSubject.put("Z2", subject.get("Z2"));
        
        FileWriter file = new FileWriter("./data/" + USERS_INDEX_FILENAME);
        file.write(array.toJSONString()); 
        file.flush();
        file.close();
    }
    
    public void addSubjectToUserIndex(JSONObject subject, String username) throws IOException, ParseException {
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
        
        ((JSONArray)user.get("subjects")).add(subject);

        FileWriter file = new FileWriter("./data/" + USERS_INDEX_FILENAME);
        file.write(array.toJSONString()); 
        file.flush();
        file.close();
    }

    // add subject to user within extended database
    public void writeUserIndex(JSONObject user) throws IOException, ParseException {
        JSONArray array = readAllUserIndex();
        array.add(user);

        FileWriter file = new FileWriter("./data/" + USERS_INDEX_FILENAME);
        file.write(array.toJSONString()); 
        file.flush();
        file.close();
    }
}

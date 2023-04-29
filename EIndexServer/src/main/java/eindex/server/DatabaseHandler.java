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

/**
 *
 * @author Jovan
 */
public class DatabaseHandler {
    final private static String USERS_FILENAME = "users.txt";
    
    public DatabaseHandler() {}
    
    static private BufferedReader createBr() throws FileNotFoundException {
        return new BufferedReader(new FileReader("./data/" + USERS_FILENAME));
    }
    
    public Collection<User> readAllUsers() throws IOException {
        BufferedReader br = createBr();
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
    
    public User readUser(String data, int data_index) throws IOException {
        BufferedReader br = createBr();
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
}

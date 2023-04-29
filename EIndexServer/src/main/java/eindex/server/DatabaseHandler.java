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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jovan
 */
public class DatabaseHandler {
    private BufferedReader br;
    final private static String usersFilename = "users.txt";
    
    public DatabaseHandler() throws FileNotFoundException {
        try {
            br = new BufferedReader(new FileReader("./data/" + usersFilename));
            br.mark(0);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Collection<User> readAllUsers() throws IOException {
        Collection<User> users = new ArrayList<>();
        String line;
        br.mark(0);
        br.reset();
        while ((line = br.readLine())!= null) {
            String[] userInfo = line.split(";");
            if (userInfo.length == 3) {
                users.add(new User(userInfo[0], userInfo[1], userInfo[2]));
            } else {
                throw new IOException("There is missing/too much user info");
            }
        }
        return users;
    }
    
    public User readUser(String data, int data_index) throws IOException {
        String line;
        br.mark(0);
        br.reset();
        while ((line = br.readLine())!= null) {
            String[] userInfo = line.split(";");
            if (userInfo.length == 3) {
                if (userInfo[data_index].equalsIgnoreCase(data)) {
                    return new User(userInfo[0], userInfo[1], userInfo[2]);
                }
            } else {
                throw new IOException("There is missing/too much user info");
            }
        }
        return null;
    }
    
    @Override
    protected void finalize()
    {  
        try { 
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eindex.client;

import java.io.PrintWriter;
import java.net.Socket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Jovan
 */
public abstract class MenuScreen extends javax.swing.JFrame {
    final protected StartupScreen parent;
    final protected PrintWriter pw;
    final protected String role;
    final protected String userName;
    final protected String firstName;
    final protected String lastName;
    final protected String jmbg;
    
    public MenuScreen(StartupScreen parent, JSONObject jUserData, String screenName) {
        super(screenName);

        this.parent = parent;
        pw = parent.getPw();
        
        userName = jUserData.get("username").toString();
        firstName = jUserData.get("first name").toString();
        lastName = jUserData.get("last name").toString();
        jmbg = jUserData.get("jmbg").toString();
        role = jUserData.get("role").toString();
    }
    
    public abstract void updateData(Object data);
    
    final public void closingWindowAction() {
        parent.setEnabled(true);
        if (parent.isSocketDead()) {
            parent.handleConnectAssets(true);
            parent.handleReopenMenuAssets(false);
        } else {
            parent.handleConnectAssets(false);
            parent.handleReopenMenuAssets(true);
        }
        parent.toFront();
        parent.requestFocus();
        this.dispose();
    }
    
    final public String getRole() {
        return role;
    }
}

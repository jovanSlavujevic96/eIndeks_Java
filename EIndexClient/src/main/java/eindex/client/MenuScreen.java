/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eindex.client;

import java.io.PrintWriter;
import javax.swing.JComboBox;
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
    
    final protected String getSelectedString(JComboBox<String> selector) {
        if (selector.getSelectedItem() != null) {
            return selector.getSelectedItem().toString();
        }
        return null;
    }
    
    final public void requestRefreshData() {
        JSONObject req = new JSONObject();
        req.put("method", "refresh");
        req.put("username", userName);
        pw.println(req);
    }
    
    final public String getRole() {
        return role;
    }
}

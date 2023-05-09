package eindex.client;

import java.io.PrintWriter;
import javax.swing.JComboBox;
import org.json.simple.JSONObject;

public abstract class MenuScreen extends javax.swing.JFrame {
    // user info storages
    final protected String role;
    final protected String userName;
    final protected String firstName;
    final protected String lastName;
    final protected String jmbg;
    
    // other important refs
    final protected StartupScreen parent;
    final protected PrintWriter pw;
    
    public MenuScreen(StartupScreen parent, JSONObject jsonUserData, String screenName) {
        super(screenName);

        this.parent = parent;
        pw = parent.getPw();
        
        // set user info from JSON received from server
        userName = jsonUserData.get("username").toString();
        firstName = jsonUserData.get("first name").toString();
        lastName = jsonUserData.get("last name").toString();
        jmbg = jsonUserData.get("jmbg").toString();
        role = jsonUserData.get("role").toString();
    }

    // abstract method which should be differently implemented for derived classes
    public abstract void updateData(Object data);
    
    // closing menu screen handling
    final public void closingWindowAction() {
        // enable parent screen
        parent.setEnabled(true);

        // check is there connection with server
        if (parent.isSocketDead()) {
            // no connection -> turn back connect UI assets, hide reopen menu and logout buttons
            parent.handleConnectAssets(true);
            parent.handleReopenMenuAssets(false);
            parent.enableLogoutBtn(false);
        } else {
            // connection exists -> hide connect UI assets and turn back reopen menu and logout buttons
            parent.handleConnectAssets(false);
            parent.handleReopenMenuAssets(true);
            parent.enableLogoutBtn(true);
        }
        // put parent screen to front
        parent.toFront();
        parent.requestFocus();

        // destruct this menu screen
        this.dispose();
    }
    
    // helper method to extract string from selector
    final protected String getSelectedString(JComboBox<String> selector) {
        if (selector.getSelectedItem() != null) {
            return selector.getSelectedItem().toString();
        }
        return null;
    }

    // helper method to form refresh data JSON req
    public JSONObject formRefreshDataReq() {
        JSONObject jsonReq = new JSONObject();
        jsonReq.put("method", "refresh");
        jsonReq.put("username", userName);
        return jsonReq;
    }

    // helper method to request refresh data from server
    final public void requestRefreshData() {
        pw.println(formRefreshDataReq());
    }

    /**
     * @return role of current user
     */
    final public String getRole() {
        return role;
    }
}

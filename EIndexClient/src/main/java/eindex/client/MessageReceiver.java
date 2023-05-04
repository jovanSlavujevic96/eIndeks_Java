package eindex.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Jovan
 */
public class MessageReceiver implements Runnable {
    final private BufferedReader br;
    final private PrintWriter pw;
    final private StartupScreen parent;
    private MenuScreen menu; // current instance of menu screen
    private JFrame focusedScreen; // screen to whom are dialog messages are going to be linked
    private JSONObject jUserData; // up-to-date JSON data from server
    
    public MessageReceiver(StartupScreen parent) {
        this.parent = parent;
        this.br = parent.getBr();
        this.pw = parent.getPw();

        // initially focus is on startup screen
        focusedScreen = parent;
    }
    
    public MenuScreen getMenu() {
        return menu;
    }
    public void setMenu(MenuScreen menu) {
        this.menu = menu;
    }
    
    public JSONObject getJUserData() {
        return jUserData;
    }
    public void setJUserData(JSONObject jUserData) {
        this.jUserData = jUserData;
    }
    
    private String processMessage(String msg) {
        // parses string in JSON regulatives
        JSONParser parser = new JSONParser();
        try {
            // parse string to JSON object
            JSONObject in = (JSONObject)parser.parse(msg);

            // get mandatory JSON message props
            String status = (in.get("status") != null) ? in.get("status").toString() : "";
            String message = (in.get("message") != null) ? in.get("message").toString() : "";
            String role = (in.get("role") != null) ? in.get("role").toString() : "";
            String method = (in.get("method") != null) ? in.get("method").toString() : "";

            // if parent is disabled it means that menu is on focuse ...
            focusedScreen = parent.isEnabled() ? parent : menu.isEnabled() ? menu : null;
            
            // checks for message dialog appearence
            if (status.contentEquals("") || message.contentEquals("") ||    // there always has to be status and message
               (status.contentEquals("200") && // if status code is 200 (success) -> method and role must exist
                    (method.contentEquals("") || role.contentEquals("")))) {
                JOptionPane.showMessageDialog(
                    focusedScreen,
                    "Na poslati zahtev nije dobijen potpun odgovor od strane servera",
                    "Bez statusa",
                    JOptionPane.WARNING_MESSAGE
                );
            } else if (status.contentEquals("200") &&
                       in.get("background") != null &&
                       in.get("background").toString().equalsIgnoreCase("true")) {
                // if there is a background attribute just do nothing - don't show any dialog
            } else {
                JOptionPane.showMessageDialog(
                    focusedScreen,
                    message,
                    status,
                    status.contentEquals("200") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
                );
            }
            
            // check if status is OK
            if (status.contentEquals("200")) {
                // different handling per various method (request) types
                if (method.equalsIgnoreCase("login")) {
                    // for succesfull "login" response
                    // make sure that both admin and student have their own JSON stored (admin data is just bigger)
                    // make sure that both of them have their of menu screen opened (Admin or Student menu screen)
                    // disable and hide necessary UI facilities
                    if (role.equalsIgnoreCase("student") || role.equalsIgnoreCase("admin")) {
                        jUserData = (JSONObject)in.get("data");
                        parent.showLogoutBtn(true);
                        parent.enableLogoutBtn(true);
                        parent.openMenuScreen();
                    }
                } else if (method.equalsIgnoreCase("refresh")) {
                    // for succesfull "refresh" response
                    // replace list of subjects in student's JSON or users in admin's JSON
                    // make sure that data is updated visually
                    if (role.equalsIgnoreCase("student") && menu.getRole().equalsIgnoreCase("student")) {
                        if (in.get("subjects") instanceof JSONArray jSubjects) {
                            jUserData.replace("subjects", jSubjects);
                            menu.updateData(jSubjects);
                        }
                    } else if (role.equalsIgnoreCase("admin") && menu.getRole().equalsIgnoreCase("admin")) {
                        if (in.get("users") instanceof JSONArray jUsers) {
                            jUserData.replace("users", jUsers);
                            menu.updateData(jUsers);
                        }
                    }
                } else if (method.equalsIgnoreCase("updateSubject")) {
                    // "updateSubject" is admin method only
                    // for succesfull "updateSubject" response
                    // make sure that summary (points) and grade is calculated succesfully
                    if (menu instanceof AdminMenuScreen admenu) {
                        admenu.updateSelectedSubject();
                    }
                } else if (method.equalsIgnoreCase("crateNewUser") ||
                           method.equalsIgnoreCase("addNewSubject")) {
                    // "createNewUser" or "addNewSubject" are admin methods only
                    // for succesfull response for these methods just request data refresh in background,
                    // which means no dialog on server response
                    if (role.equalsIgnoreCase("admin")) {
                        JSONObject refreshDataReq = menu.formRefreshDataReq();
                        refreshDataReq.put("background", "true");
                        // return new JSON req in string form
                        return refreshDataReq.toJSONString();
                    }
                }
            }
        } catch (ParseException ex) {
            // bad answer from server -> bad JSON format
            Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        // returning null means client doesn't need to answer anything to server
        return null;
    }

    @Override
    public void run() {
        // running thread loop
        while (true) {
            String recvMsg;
            String sendMsg;
            
            // receive message from server
            try {
                recvMsg = this.br.readLine();
            } catch (IOException ex) {
                break;
            }
            if (recvMsg == null) {
                break;
            }
            
            // process received message
            sendMsg = processMessage(recvMsg);

            // if there's something to answer -> do it
            if (sendMsg != null && !sendMsg.equals("")) {
                pw.println(sendMsg);
            }
        }
        // break of connection reporting
        JOptionPane.showMessageDialog(
            focusedScreen,
            "Konekcija sa serverom je prekinuta... Pokusajte se povezati ponovo",
            "Pukla veza",
            JOptionPane.ERROR_MESSAGE
        );

        // closing necessary utilities
        parent.closeSocket();
        parent.enableLogoutBtn(false);
        focusedScreen = parent;
        if (menu != null) {
            menu.closingWindowAction();
            menu = null;
        }
    }
}

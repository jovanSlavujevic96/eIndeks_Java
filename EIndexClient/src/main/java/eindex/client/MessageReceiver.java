/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eindex.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MessageReceiver implements Runnable {
    final private StartupScreen parent;
    final private BufferedReader br;
    final private PrintWriter pw;
    private MenuScreen menu;
    private JFrame focusedScreen;
    private JSONObject jUserData;
    
    public MessageReceiver(StartupScreen parent) {
        this.parent = parent;
        this.br = parent.getBr();
        this.pw = parent.getPw();
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
        JSONParser parser = new JSONParser();
        try {
            JSONObject in = (JSONObject)parser.parse(msg);

            String status = (in.get("status") != null) ? in.get("status").toString() : "";
            String message = (in.get("message") != null) ? in.get("message").toString() : "";
            String role = (in.get("role") != null) ? in.get("role").toString() : "";
            String method = (in.get("method") != null) ? in.get("method").toString() : "";
            focusedScreen = parent.isEnabled() ? parent : menu.isEnabled() ? menu : null;
            
            if (status.contentEquals("") || message.contentEquals("") ||    // there always has to be status and message
               (status.charAt(0) == '2' && // if status code is 20X -> method and role must exist
                    (method.contentEquals("") || role.contentEquals("")))) {
                JOptionPane.showMessageDialog(
                    focusedScreen,
                    "Na poslati zahtev nije dobijen potpun odgovor od strane servera",
                    "Bez statusa",
                    JOptionPane.WARNING_MESSAGE
                );
            } else if (status.charAt(0) == '2' &&
                       in.get("background") != null &&
                       in.get("background").toString().equalsIgnoreCase("true")) {
                // if there is a background attribute do not show nothing
            } else {
                JOptionPane.showMessageDialog(
                    focusedScreen,
                    message,
                    status,
                    (status.charAt(0) == '2') ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
                );
            }
            
            // check if status is OK
            if (status.charAt(0) == '2') {
                if (method.equalsIgnoreCase("login")) {
                    if (role.equalsIgnoreCase("student") || role.equalsIgnoreCase("admin")) {
                        jUserData = (JSONObject)in.get("data");
                        parent.openMenuScreen();
                    }
                } else if (method.equalsIgnoreCase("refresh")) {
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
                    if (menu instanceof AdminMenuScreen admenu) {
                        admenu.updateSelectedSubject();
                    }
                } else if (method.equalsIgnoreCase("crateNewUser")) {
                    if (role.equalsIgnoreCase("admin")) {
                        // request refresh because of new user
                        JSONObject refreshDataReq = menu.formRefreshDataReq();
                        refreshDataReq.put("background", "true");
                        return refreshDataReq.toJSONString();
                    }
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void run() {
        while (true) {
            String recvMsg;
            String sendMsg;
            
            // recv
            try {
                recvMsg = this.br.readLine();
            } catch (IOException ex) {
                break;
            }
            if (recvMsg == null) {
                break;
            }
            
            // process
            sendMsg = processMessage(recvMsg);
            
            if (sendMsg != null && !sendMsg.equals("")) {
                pw.println(sendMsg);
            }
        }
        JOptionPane.showMessageDialog(
            focusedScreen,
            "Konekcija sa serverom je prekinuta... Pokusajte se povezati ponovo",
            "Pukla veza",
            JOptionPane.ERROR_MESSAGE
        );
        parent.closeSocket();
        focusedScreen = parent;
        if (menu != null) {
            menu.closingWindowAction();
            menu = null;
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eindex.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
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
    private StudentMenuScreen menu;
    private JFrame focusedScreen;
    
    public MessageReceiver(StartupScreen parent) {
        this.parent = parent;
        this.br = parent.getBr();
        this.pw = parent.getPw();
        focusedScreen = parent;
    }
    
    public static <T, U> List<U> convertStringListToIntList(List<T> listOfString, Function<T, U> function)
    {
        return listOfString.stream()
            .map(function)
            .collect(Collectors.toList());
    }
    
    private String processMessage(String msg) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject in = (JSONObject)parser.parse(msg);

            String status = (in.get("status") != null) ? in.get("status").toString() : "";
            String message = (in.get("message") != null) ? in.get("message").toString() : "";
            String role = (in.get("role") != null) ? in.get("role").toString() : "";
            String method = (in.get("method") != null) ? in.get("method").toString() : "";
            focusedScreen = (method.equalsIgnoreCase("login")) ?
                    parent : (method.equalsIgnoreCase("refreshGrades")) ?
                    menu : null;
            
            if (status.contentEquals("") || message.contentEquals("") || method.contentEquals("") ||
               (method.equalsIgnoreCase("login") && role.contentEquals(""))) {
                JOptionPane.showMessageDialog(
                    focusedScreen,
                    "Na poslati zahtev nema odgovora od strane servera",
                    "Bez statusa",
                    JOptionPane.WARNING_MESSAGE
                );
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
                    if (role.equalsIgnoreCase("student")) {
                        JSONObject jIndex = (JSONObject)in.get("index");

                        // Create and display the form
                        java.awt.EventQueue.invokeLater(() -> {
                            menu = new StudentMenuScreen(parent, jIndex);
                            menu.setVisible(true);

                            parent.handleLoginAssets(false);
                            parent.setEnabled(false);
                        });
                    } else if (role.equalsIgnoreCase("admin")) {

                    }
                } else if (method.equalsIgnoreCase("refreshGrades")) {
                    if (role.equalsIgnoreCase("student")) {
                        JSONArray jSubjects = (JSONArray)in.get("subjects");
                        menu.setSubjects(jSubjects);
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
        focusedScreen = parent;
        parent.setEnabled(true);
        parent.handleConnectAssets(true);
        menu.dispose();
        menu = null;
        parent.toFront();
        parent.requestFocus();
    }
}

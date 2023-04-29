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
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MessageReceiver implements Runnable {
    final private StartupScreen parent;
    final private BufferedReader br;
    final private PrintWriter pw;
    
    public MessageReceiver(StartupScreen parent) {
        this.parent = parent;
        this.br = parent.getBr();
        this.pw = parent.getPw();
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
            
            if (status.contentEquals("") || message.contentEquals("") || role.contentEquals("")) {
                JOptionPane.showMessageDialog(
                    parent,
                    "Na poslati zahtev nema odgovora od strane servera",
                    "Bez statusa",
                    JOptionPane.WARNING_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    parent,
                    message,
                    status,
                    (status.charAt(0) == '2') ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
                );
            }
            
            // if status is OK
            if (status.charAt(0) == '2') {
                if (role.equalsIgnoreCase("student")) {
                    JSONObject jIndex = (JSONObject)in.get("index");

                    /* Create and display the form */
                    java.awt.EventQueue.invokeLater(() -> {
                        MenuScreen menu = new MenuScreen(parent, jIndex);
                        menu.setVisible(true);
                        menu.setAlwaysOnTop(true);
                    });
                } else if (role.equalsIgnoreCase("admin")) {
                    
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
                Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
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
    }

}

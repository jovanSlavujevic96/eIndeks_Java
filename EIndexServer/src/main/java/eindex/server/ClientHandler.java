/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eindex.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jovan
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private String userName;
    private BufferedReader br;  // for recv
    private PrintWriter pw;     // for send
    private Consumer<ClientHandler> logoutMethod;
    
    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public Socket getSocket() {
        return this.socket;
    }
    
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    public PrintWriter getPw() {
        return pw;
    }
    
    public static int generateRandomNum(int min, int max) {
        return (int)Math.floor(Math.random()*(max-min+1) + min);
    }
    
    public ClientHandler(Socket socket, Consumer<ClientHandler> logoutMethod) throws IOException {
        this.socket = socket;
        this.logoutMethod = logoutMethod;
        userName = "";
        
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        } catch (IOException ex) {
            // just disclaimer
            throw ex;
        }
    }
    
    public String processMessage(String msg) {
        String out = "";

        return out;
    }
    
    @Override
    public void run() {
        while (true) {
            String msg;

            // recv
            try {
                msg = this.br.readLine();
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
            if (msg == null) {
                System.out.println("Message rcv for client \"" + userName + "\" is null");
                break;
            }
            
            System.out.println(msg);

            // process
            msg = processMessage(msg);

            // send
            if (!msg.equals("")) {
                pw.println(msg);
            }
        }

        // exit
        try {
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        logoutMethod.accept(this);
    }
}

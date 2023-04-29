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
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
    private DatabaseHandler dbHandler;
    
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
    
    public void bindDbHandler(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }
    
    public static int generateRandomNum(int min, int max) {
        return (int)Math.floor(Math.random()*(max-min+1) + min);
    }
    
    private String hash(String inputStr) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(inputStr.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException ex) {
            // cannot occur -> MD5 exists
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
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
        JSONObject out = new JSONObject();
        JSONParser parser = new JSONParser();
		
        try {
            // parse JSON Object
            JSONObject in = (JSONObject)parser.parse(msg);

            // look for method
            String method;
            if (in.get("method") != null) {
                method = in.get("method").toString();
            } else {
                out.put("status", "400");
                out.put("message", "Method is missing");
                return out.toJSONString();
            }

            // "login" method
            if (method.contentEquals("login")) {
                
                // get username
                userName = "";
                if (in.get("username") != null) {
                    userName = in.get("username").toString();
                } else {
                    out.put("status", "400");
                    out.put("message", "Username is missing");
                    return out.toJSONString();
                }
                
                // get password
                String password;
                if (in.get("password") != null) {
                    password = in.get("password").toString();
                } else {
                    out.put("status", "400");
                    out.put("message", "Password is missing");
                    return out.toJSONString();
                }
                
                // get role
                String role;
                if (in.get("role") != null) {
                    role = in.get("role").toString();
                } else {
                    out.put("status", "400");
                    out.put("message", "Role not found");
                    return out.toJSONString();
                }
                
                // check is role a good one
                if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("student")) {
                    out.put("status", "401");
                    out.put("message", "Bad role for user " + userName);
                    return out.toJSONString();
                }
                
                // try to find a user within database
                User user = dbHandler.readUserPer(userName, 0);
                if (user != null) {
                    if (role.equalsIgnoreCase(user.getRole())) {
                        // hash password from database to compare it to hashed password from client
                        String hashedDbPass = hash(user.getPassword());
                        // check does password match
                        if (hashedDbPass.contentEquals(password)) {
                            out.put("status", "200");
                            out.put("message", user.getRole() + " " + userName + " succesfully logged in");

                            JSONObject userIndex = dbHandler.readUserIndexPer(userName, "username");
                            out.put("index", userIndex);
                        } else {
                            out.put("status", "401");
                            out.put("message", "Bad password for user " + userName);
                        }
                    } else {
                        out.put("status", "401");
                        out.put("message", "Bad role for user " + userName);
                    }
                } else {
                    out.put("status", "404");
                    out.put("message", "User " + userName + " has not been found");
                    userName = "";
                }
            } else {
                out.put("status", "405");
                out.put("message", "Method not allowed or not exist");
            }
        } catch (ParseException | IOException pe) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, pe);
            out.put("status", "500");
            out.put("message", "Internal server error");
        }
        return out.toJSONString();
    }
    
    @Override
    public void run() {
        while (true) {
            String msg;

            // recv
            try {
                msg = this.br.readLine();
            } catch (IOException ex) {
                System.out.println("Client \"" + userName + "\" disconnected");
                break;
            }
            if (msg == null) {
                System.out.println("Message rcv for client \"" + userName + "\" is null");
                break;
            }

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
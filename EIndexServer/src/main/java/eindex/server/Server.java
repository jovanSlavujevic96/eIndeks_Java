/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eindex.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jovan
 */
public class Server {
    private ServerSocket acceptSocket;
    private final Collection<ClientHandler> clients;
    private DatabaseHandler dbHandler;
    
    public void acceptClients() throws IOException {
        Socket client_socket;
        Thread client_thread;
        ClientHandler client_handler;

        while (true) {
            System.out.println("Waiting for new clients..");
            try {
                client_socket = this.acceptSocket.accept();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
            
            if (client_socket == null) {
                System.out.println("Accept client returned null socket");
                break;
            }
            try {
                client_handler = new ClientHandler(client_socket, (n) -> { clients.remove(n); });
                client_handler.bindDbHandler(this.getDbHandler());
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
            
            clients.add(client_handler);
            client_thread = new Thread(client_handler);
            client_thread.start();
        }
        
        // safe exit
        for (ClientHandler client : clients) {
            client.getSocket().shutdownInput();
            client.getSocket().close();
        }
        acceptSocket.close();
    }
    
    void bindDbHandler(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }
    
    DatabaseHandler getDbHandler() {
        return dbHandler;
    }
    
    public Server(int port) throws IOException {
        try {
            this.acceptSocket = new ServerSocket(port);
        } catch (IOException ex) {
            // just disclaimer
            throw ex;
        }
        clients = new ArrayList();
    }
    
    public static void main(String[] args) {
        Server server;
        DatabaseHandler db = new DatabaseHandler();
        
        final int port = 5050;
        
        try {
            Collection<User> users = db.readAllUsers();
            for (User user : users) {
                System.out.println(
                        "User name: " + user.getUsername() + " ; " +
                        "password: " + user.getPassword() + " ; " +
                        "role: " + user.getRole()
                );
            }
            
            server = new Server(port);
            server.bindDbHandler(db);
            server.acceptClients();

        } catch (IOException ex) {
            System.out.println("StickerServer create failed: " + ex.toString());
        }
    }
}

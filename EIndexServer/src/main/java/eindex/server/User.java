/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eindex.server;

/**
 *
 * @author Jovan
 */
public class User {
    // wrapper for storage data
    private String username;
    private String password;
    private String role;

    public User(String name, String pass, String rol) {
        username = name;
        password = pass;
        role = rol;
    }
    
    public String getUsername() {
        return username;
    }
        
    public String getPassword() {
        return password;
    }
    
    public String getRole() {
        return role;
    }
    
    @Override
    public String toString() {
        return username + ";" + password + ";" + role;
    }
}

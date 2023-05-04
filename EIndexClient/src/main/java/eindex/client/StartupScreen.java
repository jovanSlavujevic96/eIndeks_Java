package eindex.client;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.NumberFormat;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.text.NumberFormatter;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.json.simple.JSONObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.WindowConstants;

public class StartupScreen extends javax.swing.JFrame {    
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private MessageReceiver rmfs;
    
    public BufferedReader getBr() {
        return br;
    }
    
    public PrintWriter getPw() {
        return pw;
    }
    
    public boolean isSocketDead() {
        return socket == null || socket.isClosed() || !socket.isConnected();
    }
    
    public void enableLogoutBtn(boolean enable) {
        bLogout.setEnabled(enable);
    } 
    
    public void showLogoutBtn(boolean show) {
        bLogout.setVisible(show);
    }
    
    public void closeSocket() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
            if (pw != null) {
                pw.close();
                pw = null;
            }
            if (br != null) {
                br.close();
                br = null;
            }
        } catch (IOException ex) {
            Logger.getLogger(StartupScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // make MD5 hash from entered string
    private String hash(String inputStr) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(inputStr.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException ex) {
            // cannot occur -> MD5 exists
            Logger.getLogger(StartupScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public void handleLoginAssets(boolean enable) {
        if (enable) {
            jInputUsername.setEnabled(true);
            jInputUsername.setToolTipText("Unesite korisnicko ime");
            jInputPassword.setEnabled(true);
            jInputPassword.setToolTipText("Unesite lozinku");
            bLogin.setEnabled(true);
            bLogin.setVisible(true);
            bShowPass.setEnabled(true);
            bShowPass.setVisible(true);
            jSelectRole.setEnabled(true);
        } else {
            jInputUsername.setEnabled(false);
            jInputUsername.setToolTipText("");
            jInputPassword.setEnabled(false);
            jInputPassword.setToolTipText("");
            bLogin.setEnabled(false);
            bLogin.setVisible(false);
            bShowPass.setEnabled(false);
            bShowPass.setVisible(false);
            jSelectRole.setEnabled(false);
        }
    }
    
    public void handleConnectAssets(boolean enable) {
        if (enable) {
            btnConnect.setEnabled(true);
            btnConnect.setVisible(true);
            jInputIp.setEnabled(true);
            jInputIp.setToolTipText("Unesite IP adresu servera");
            jInputPort.setEnabled(true);
            jInputPort.setToolTipText("Unesi port servera");
        } else {
            btnConnect.setEnabled(false);
            btnConnect.setVisible(false);
            jInputIp.setEnabled(false);
            jInputIp.setToolTipText("");
            jInputPort.setEnabled(false);
            jInputPort.setToolTipText("");
        }
    }
    
    public void handleReopenMenuAssets(boolean enable) {
        if (enable) {
            bReopenMenu.setEnabled(true);
            bReopenMenu.setVisible(true);
        } else {
            bReopenMenu.setEnabled(false);
            bReopenMenu.setVisible(false);
        }
    }
    
    public void openMenuScreen() {
        // Create and display the form
        java.awt.EventQueue.invokeLater(() -> {
            MenuScreen menu;
            String role = rmfs.getJUserData().get("role").toString();

            // decide by role which derived MenuScreen to create
            if (role.equalsIgnoreCase("student")) {
                menu = new StudentMenuScreen(this, rmfs.getJUserData());
            } else if (role.equalsIgnoreCase("admin")) {
                menu = new AdminMenuScreen(this, rmfs.getJUserData());
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Nije moguce otvoriti meni... Ne postoji meni za rolu \"" + role + "\"",
                    "Meni greska",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            // add current running menu ref
            rmfs.setMenu(menu);

            menu.setVisible(true);
            // make sure that closing of menu screen will not collapse/terminate app
            menu.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            // add closing event for menu screen
            menu.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        menu.closingWindowAction();
                   }
                });

            // hide/disable login assets
            this.handleLoginAssets(false);
            // make startup screen disabled (inactive/unusable)
            this.setEnabled(false);
            // hide reopen menu button
            this.handleReopenMenuAssets(false);
        });
    }
    
    public StartupScreen() {
        super("Pocetna stranica");
        initComponents();
    }
    
    private void login() {
        String username = jInputUsername.getText();
        String password = jInputPassword.getText();
        String role = jSelectRole.getSelectedItem().toString();

        // if username or password are not entered report error through dialog
        if ((username == null || username.contentEquals("")) ||
            (password == null || password.contentEquals(""))) {
            JOptionPane.showMessageDialog(
                    this,
                    "Morate uneti kredencijale",
                    "Kredencijali",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // hash the password
        password = hash(password);
        
        // packing user info to JSON req
        JSONObject obj = new JSONObject();
        obj.put("username", username.toLowerCase());
        obj.put("password", password);
        obj.put("role", role);
        obj.put("method", "login");

        // send req to server
        pw.println(obj);
    }
    
    private void connect() {
        String ip = jInputIp.getText();
        Integer port = (Integer)((JFormattedTextField)jInputPort).getValue();
        
        // Validate an IPv4 address
        InetAddressValidator validator = InetAddressValidator.getInstance();
        if (!validator.isValidInet4Address(ip)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Uneta IP adresa \"" + ip + "\" nije validna",
                    "IP",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        try {
            this.socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 2000);
            this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.pw = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Neuspesno povezivanje sa \"" + ip + ":" + port + "\" serverom",
                    "Konekcija neuspesna",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // create and start message receiver thread
        this.rmfs = new MessageReceiver(this);
        Thread thr = new Thread(rmfs);
        thr.start();

        // disable connect & enable login UI assets
        handleConnectAssets(false);
        handleLoginAssets(true);

        // inform about succesfull connection
        JOptionPane.showMessageDialog(
                this,
                "Uspesno povezivanje sa \"" + ip + ":" + port + "\" serverom",
                "Konekcija uspesna",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnConnect = new javax.swing.JButton();
        jInputIp = new javax.swing.JTextField();
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        jInputPort = new javax.swing.JFormattedTextField(formatter);
        bLogin = new javax.swing.JButton();
        bLogin.setEnabled(false);
        jInputUsername = new javax.swing.JTextField();
        jInputUsername.setEnabled(false);
        jInputPassword = new javax.swing.JPasswordField();
        jInputPassword.setEnabled(false);
        bShowPass = new ShowHideButton((javax.swing.JPasswordField)jInputPassword);
        bShowPass.setVisible(false);
        jSelectRole = new javax.swing.JComboBox<>();
        jSelectRole.setEnabled(false);
        bReopenMenu = new javax.swing.JButton();
        handleReopenMenuAssets(false);
        bLogout = new javax.swing.JButton();
        bLogout.setEnabled(false);
        bLogout.setVisible(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnConnect.setText("Povezi se");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        jInputIp.setText("127.0.0.1");
        jInputIp.setToolTipText("Unesite IP adresu servera");
        jInputIp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jInputIpKeyPressed(evt);
            }
        });

        jInputPort.setText("5050");
        jInputPort.setToolTipText("Unesite port servera");
        jInputPort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jInputPortKeyPressed(evt);
            }
        });

        bLogin.setText("Prijavi se");
        bLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLoginActionPerformed(evt);
            }
        });

        jInputUsername.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jInputUsernameKeyPressed(evt);
            }
        });

        jInputPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jInputPasswordKeyPressed(evt);
            }
        });

        jSelectRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Student", "Admin" }));

        bReopenMenu.setText("Otvori Meni");
        bReopenMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bReopenMenuActionPerformed(evt);
            }
        });

        bLogout.setText("Odjavi se");
        bLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jInputIp, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSelectRole, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jInputPort, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnConnect))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jInputUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jInputPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bShowPass, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(bLogout)
                                    .addComponent(bLogin)))))
                    .addComponent(bReopenMenu))
                .addContainerGap(60, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnConnect, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jInputPort, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jInputIp, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jSelectRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jInputUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jInputPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(bShowPass, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 240, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bReopenMenu)
                    .addComponent(bLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        // perfom connect method on connect button press
        connect();
    }//GEN-LAST:event_btnConnectActionPerformed

    private void bLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bLoginActionPerformed
        // perform login method on login button press
        login();
    }//GEN-LAST:event_bLoginActionPerformed

    private void bReopenMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bReopenMenuActionPerformed
        // perform open menu screen on reopen button press
        openMenuScreen();
    }//GEN-LAST:event_bReopenMenuActionPerformed

    private void jInputPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jInputPasswordKeyPressed
        // if password input is selected perform login on ENTER keyboard press
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            login();
        }
    }//GEN-LAST:event_jInputPasswordKeyPressed

    private void jInputUsernameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jInputUsernameKeyPressed
        // if username input is selected perform login on ENTER keyboard press
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            login();
        }
    }//GEN-LAST:event_jInputUsernameKeyPressed

    private void jInputIpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jInputIpKeyPressed
        // if IP input is selected perform connect on ENTER keyboard press
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            connect();
        }
    }//GEN-LAST:event_jInputIpKeyPressed

    private void jInputPortKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jInputPortKeyPressed
        // if port input is selected perform connect on ENTER keyboard press
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            connect();
        }
    }//GEN-LAST:event_jInputPortKeyPressed

    private void bLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bLogoutActionPerformed
        // reset JSON storage from message receiver
        rmfs.setJUserData(null);

        // show/enable login assets & hide/disable reopen menu
        handleLoginAssets(true);
        handleReopenMenuAssets(false);

        // hide & disable logout button
        bLogout.setEnabled(false);
        bLogout.setVisible(false);
    }//GEN-LAST:event_bLogoutActionPerformed
        
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StartupScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new StartupScreen().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bLogin;
    private javax.swing.JButton bLogout;
    private javax.swing.JButton bReopenMenu;
    private javax.swing.JButton bShowPass;
    private javax.swing.JButton btnConnect;
    private javax.swing.JTextField jInputIp;
    private javax.swing.JTextField jInputPassword;
    private javax.swing.JTextField jInputPort;
    private javax.swing.JTextField jInputUsername;
    private javax.swing.JComboBox<String> jSelectRole;
    // End of variables declaration//GEN-END:variables
}

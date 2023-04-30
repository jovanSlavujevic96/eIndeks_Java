/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package eindex.client;

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
import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.text.NumberFormatter;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.json.simple.JSONObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.WindowConstants;

/**
 *
 * @author Jovan
 */
public class StartupScreen extends javax.swing.JFrame {    
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private MessageReceiver rmfs;
    private String userName;
    private boolean showPassword = false;
    final static private ImageIcon showIcon = new ImageIcon("./resources/show_pass_small.png");
    final static private ImageIcon showIconHover = new ImageIcon("./resources/show_pass_small_framed.png");
    final static private ImageIcon hideIcon = new ImageIcon("./resources/hide_pass_small.png");
    final static private ImageIcon hideIconHover = new ImageIcon("./resources/hide_pass_small_framed.png");
    
    public BufferedReader getBr() {
        return br;
    }
    
    public PrintWriter getPw() {
        return pw;
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUsername(String username) {
        this.userName = username;
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
    
    public void reopenMenuScreen() {
        // Create and display the form
        java.awt.EventQueue.invokeLater(() -> {
            StudentMenuScreen menu = new StudentMenuScreen(this, rmfs.getJIndex());
            rmfs.setMenu(menu);
            
            menu.setVisible(true);
            menu.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            menu.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        menu.closingWindowAction();
                   }
                });

            this.handleLoginAssets(false);
            this.setEnabled(false);
            this.handleReopenMenuAssets(false);
        });
    }
    
    public StartupScreen() {
        super("Pocetna stranica");
        initComponents();
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
        bShowPass = new javax.swing.JButton();
        bShowPass.setIcon(showIcon);
        bShowPass.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        bShowPass.setContentAreaFilled(false);
        jSelectRole = new javax.swing.JComboBox<>();
        jSelectRole.setEnabled(false);
        bReopenMenu = new javax.swing.JButton();
        handleReopenMenuAssets(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnConnect.setText("Povezi se");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        jInputIp.setText("127.0.0.1");
        jInputIp.setToolTipText("Unesite IP adresu servera");

        jInputPort.setText("5050");
        jInputPort.setToolTipText("Unesite port servera");

        bLogin.setText("Prijavi se");
        bLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLoginActionPerformed(evt);
            }
        });

        bShowPass.setRolloverEnabled(true);
        bShowPass.setRolloverIcon(showIconHover);
        bShowPass.setVisible(false);
        bShowPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bShowPassActionPerformed(evt);
            }
        });

        jSelectRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Student", "Admin" }));

        bReopenMenu.setText("Otvori Meni");
        bReopenMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bReopenMenuActionPerformed(evt);
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
                                .addComponent(bLogin))))
                    .addComponent(bReopenMenu))
                .addContainerGap(109, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jInputPort, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jInputIp, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jSelectRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jInputUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jInputPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(bShowPass, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 244, Short.MAX_VALUE)
                .addComponent(bReopenMenu)
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
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
        
        this.rmfs = new MessageReceiver(this);
        Thread thr = new Thread(rmfs);
        thr.start();
        
        handleConnectAssets(false);
        handleLoginAssets(true);
        
        JOptionPane.showMessageDialog(
                this,
                "Uspesno povezivanje sa \"" + ip + ":" + port + "\" serverom",
                "Konekcija uspesna",
                JOptionPane.INFORMATION_MESSAGE
        );
    }//GEN-LAST:event_btnConnectActionPerformed

    private void bLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bLoginActionPerformed
        String username = jInputUsername.getText();
        String password = jInputPassword.getText();
        String role = jSelectRole.getSelectedItem().toString();
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
        // password hash
        password = hash(password);
        
        // packing userinfo to JSON
        JSONObject obj = new JSONObject();
        obj.put("username", username.toLowerCase());
        obj.put("password", password);
        obj.put("role", role);
        obj.put("method", "login");

        pw.println(obj);
    }//GEN-LAST:event_bLoginActionPerformed

    private void bShowPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bShowPassActionPerformed
        // TODO add your handling code here:
        showPassword = !showPassword;
        if (showPassword) {
            bShowPass.setRolloverIcon(hideIconHover);
            bShowPass.setIcon(hideIcon);
            ((JPasswordField)jInputPassword).setEchoChar('\u0000');
        } else {
            bShowPass.setRolloverIcon(showIconHover);
            bShowPass.setIcon(showIcon);
            ((JPasswordField)jInputPassword).setEchoChar('*');
        }
    }//GEN-LAST:event_bShowPassActionPerformed

    private void bReopenMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bReopenMenuActionPerformed
        // TODO add your handling code here:
        reopenMenuScreen();
    }//GEN-LAST:event_bReopenMenuActionPerformed
        
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

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package eindex.client;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.text.NumberFormatter;
import org.apache.commons.validator.routines.InetAddressValidator;

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
    
    public String getUserName() {
        return userName;
    }
    
    public void setUsername(String username) {
        this.userName = username;
    }
    
    /**
     * Creates new form StickerClientMainWindow
     */
    public StartupScreen() {
        initComponents();
        
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
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
        jInputUsername = new javax.swing.JTextField();
        jInputPassword = new javax.swing.JPasswordField();
        bShowPass = new javax.swing.JButton();
        bShowPass.setIcon(showIcon);
        bShowPass.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        bShowPass.setContentAreaFilled(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnConnect.setText("Povezi se");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        jInputIp.setText("127.0.0.1");

        jInputPort.setText("8080");

        bLogin.setText("Uloguj se");
        bLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLoginActionPerformed(evt);
            }
        });

        bShowPass.setRolloverEnabled(true);
        bShowPass.setRolloverIcon(showIconHover);
        bShowPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bShowPassActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnConnect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jInputIp, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jInputPort, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bLogin)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jInputUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jInputPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bShowPass, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 200, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jInputPort, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jInputIp, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bShowPass, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(bLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jInputUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jInputPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(278, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        String ip = jInputIp.getText();
        Integer port = (Integer)((JFormattedTextField)jInputPort).getValue();
        
        // Validate an IPv4 address
        InetAddressValidator validator = InetAddressValidator.getInstance();
        if (!validator.isValidInet4Address(ip)) {
            JOptionPane.showMessageDialog(this, "The IP address \"" + ip + "\" is invalid", "Bad IP", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            this.socket = new Socket(ip, port);
            this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.pw = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);

            this.rmfs = new MessageReceiver(this);
            Thread thr = new Thread(rmfs);
            thr.start();

            btnConnect.setEnabled(false);
            btnConnect.setVisible(false);
            jInputIp.setEditable(false);
            jInputPort.setEditable(false);
        } catch (IOException ex) {
            Logger.getLogger(StartupScreen.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnConnectActionPerformed

    private void bLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bLoginActionPerformed
       
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
    private javax.swing.JButton bShowPass;
    private javax.swing.JButton btnConnect;
    private javax.swing.JTextField jInputIp;
    private javax.swing.JTextField jInputPassword;
    private javax.swing.JTextField jInputPort;
    private javax.swing.JTextField jInputUsername;
    // End of variables declaration//GEN-END:variables
}

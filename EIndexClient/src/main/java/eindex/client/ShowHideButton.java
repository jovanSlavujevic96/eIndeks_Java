/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eindex.client;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPasswordField;

/**
 *
 * @author Jovan
 */
public class ShowHideButton extends JButton {
    // show/hide password props
    private boolean showPassword = false;
    final static private ImageIcon showIcon = new ImageIcon("./resources/show_pass_small.png");
    final static private ImageIcon showIconHover = new ImageIcon("./resources/show_pass_small_framed.png");
    final static private ImageIcon hideIcon = new ImageIcon("./resources/hide_pass_small.png");
    final static private ImageIcon hideIconHover = new ImageIcon("./resources/hide_pass_small_framed.png");
    
    public ShowHideButton(JPasswordField passwordInput) {
        super();

        showPassword = false;
        JButton bShowPass = this;

        bShowPass.setRolloverEnabled(true);
        bShowPass.setVisible(true);
        bShowPass.setIcon(showIcon);
        bShowPass.setRolloverIcon(showIconHover);
        bShowPass.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        bShowPass.setContentAreaFilled(false);
        
        // Listen for changes
        this.addActionListener((java.awt.event.ActionEvent evt) -> {
            showPassword = !showPassword;
            if (showPassword) {
                bShowPass.setRolloverIcon(hideIconHover);
                bShowPass.setIcon(hideIcon);
                passwordInput.setEchoChar('\u0000');
            } else {
                bShowPass.setRolloverIcon(showIconHover);
                bShowPass.setIcon(showIcon);
                passwordInput.setEchoChar('*');
            }
        });
    }
}

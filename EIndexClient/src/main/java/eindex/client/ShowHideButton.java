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

    /**
     *
     * @param passwordInput - password text filed to which this button will be linked to (showing/hiding it)
     */
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
            // on every button press switch boolean flag to opposite value
            showPassword = !showPassword;
            if (showPassword) {
                // show password means making cases visible
                // replace show button icon by hide
                bShowPass.setRolloverIcon(hideIconHover);
                bShowPass.setIcon(hideIcon);
                passwordInput.setEchoChar('\u0000');
            } else {
                // hide password means making cases like stars ***
                // replace hide button icon by show
                bShowPass.setRolloverIcon(showIconHover);
                bShowPass.setIcon(showIcon);
                passwordInput.setEchoChar('*');
            }
        });
    }
}

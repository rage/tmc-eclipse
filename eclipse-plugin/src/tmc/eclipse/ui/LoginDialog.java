package tmc.eclipse.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.google.common.base.Optional;

import fi.helsinki.cs.tmc.core.holders.TmcSettingsHolder;
import tmc.eclipse.domain.TmcCoreSettingsImpl;
import tmc.eclipse.util.LoginListener;

public class LoginDialog extends javax.swing.JDialog {

    public static void display(LoginListener onOk, final Runnable onClosed) {
        LoginDialog dialog = new LoginDialog(onOk);
        dialog.setLocationRelativeTo(null);
        dialog.pack();
        dialog.setVisible(true);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                onClosed.run();
            }
        });
    }

    private final TmcCoreSettingsImpl settings;
    private LoginListener onLogin;
    private static boolean visible;

    /**
     * Creates new form LoginForm
     */
    public LoginDialog(LoginListener onLogin) {
        initComponents();

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.settings = (TmcCoreSettingsImpl) TmcSettingsHolder.get();
        final Optional<String> username = settings.getUsername();
        if (username.isPresent()) {
            this.usernameField.setText(username.get());
        }

        if (!usernameField.getText().isEmpty()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    passwordField.requestFocusInWindow();
                }
            });
        }

        final String serverAddress = TmcSettingsHolder.get().getServerAddress();
        if (!serverAddress.isEmpty()) {
            this.addressLabel.setText(serverAddress);
        }

        this.onLogin = onLogin;
        this.visible = true;

        /* Add a windowlistener to the dialog to track when the dialog is closed
        * from the x-button
        */
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                visible = false;
                super.windowClosing(e);
            }
        });
    }

    public static boolean isWindowVisible() {
        return visible;
    }

    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        loginButton = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();
        usernameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        serverLabel = new javax.swing.JLabel();
        addressLabel = new javax.swing.JLabel();
        changeServerButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("TMC Login");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        loginButton.setText("Log in");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        titleLabel.setFont(new java.awt.Font("Ubuntu", 1, 18));
        titleLabel.setText("Log in with your mooc.fi -account");

        usernameLabel.setText("Email");

        passwordLabel.setText("Password");

        usernameField.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameFieldActionPerformed(evt);
            }
        });

        passwordField.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordFieldActionPerformed(evt);
            }
        });

        serverLabel.setText("Server address");

        changeServerButton.setText("Change");
        changeServerButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeServerButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(titleLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(serverLabel)
                        .addGap(38, 38, 38)
                        .addComponent(addressLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(changeServerButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(268, 268, 268)
                        .addComponent(loginButton)
                        .addGap(6, 6, 6)
                        .addComponent(cancelButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(usernameLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(passwordLabel)
                                .addGap(72, 72, 72)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                            .addComponent(usernameField))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(titleLabel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(changeServerButton)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(serverLabel)
                            .addComponent(addressLabel))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(usernameLabel))
                    .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(passwordLabel))
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loginButton)
                    .addComponent(cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(408, 261));
        setLocationRelativeTo(null);
    }// </editor-fold>

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
        this.dispose();
        visible = false;
    }

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
        settings.setUsername(usernameField.getText());
        settings.save();
        onLogin.setPassword(new String(passwordField.getPassword()));
        onLogin.actionPerformed(evt);

        this.setVisible(false);
        this.dispose();
        visible = false;
    }

    private void usernameFieldActionPerformed(java.awt.event.ActionEvent evt) {
        passwordField.requestFocusInWindow();
    }

    private void passwordFieldActionPerformed(java.awt.event.ActionEvent evt) {
        loginButton.doClick();
    }

    private void changeServerButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String newAddress = JOptionPane.showInputDialog(this, "Server address", this.addressLabel.getText());
        if (newAddress != null && !newAddress.trim().isEmpty()) {
            this.addressLabel.setText(newAddress.trim());
            TmcSettingsHolder.get().setServerAddress(newAddress);
        }
    }

    // Variables declaration - do not modify
    private javax.swing.JLabel addressLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton changeServerButton;
    private javax.swing.JButton loginButton;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration
}

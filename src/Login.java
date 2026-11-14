/*
 * Login Page
 * Project: National Scholarship Portal (NSP)
 * Made by: Kapil Tamoli
 * Upgraded: End-to-End MySQL JDBC Integration & Modern UI
 *
 * Authenticates students and administrators against the `users` table
 * and securely routes them to their respective dashboards.
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame implements ActionListener {

    JLabel userLabel, passLabel;
    JTextField userField;
    JPasswordField passField;
    JCheckBox showPasswordCheck;
    JButton loginButton, adminLoginButton, registerButton;

    public Login() {
        UITheme.applySystemLookAndFeel();

        setTitle("NSP - Official Login Portal");
        setSize(480, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_LIGHT);

        // ---- Header Panel ----
        JPanel headerPanel = UITheme.createHeaderPanel(
                "National Scholarship Portal",
                "Government of India | Student & Admin Authentication",
                "SECURE LOGIN"
        );
        add(headerPanel, BorderLayout.NORTH);

        // ---- Form Body Panel ----
        JPanel bodyPanel = new JPanel(null);
        bodyPanel.setBackground(UITheme.BG_LIGHT);
        bodyPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        // Subtitle / Prompt
        JLabel promptLabel = new JLabel("Please enter your credentials to access your account:");
        promptLabel.setFont(UITheme.FONT_SUBTITLE);
        promptLabel.setForeground(UITheme.TEXT_MUTED);
        promptLabel.setBounds(40, 15, 400, 20);
        bodyPanel.add(promptLabel);

        // Username / Email
        userLabel = new JLabel("Username or Email:");
        userLabel.setFont(UITheme.FONT_LABEL);
        userLabel.setForeground(UITheme.TEXT_DARK);
        userLabel.setBounds(40, 45, 180, 20);
        bodyPanel.add(userLabel);

        userField = UITheme.createTextField();
        userField.setBounds(40, 68, 385, 34);
        bodyPanel.add(userField);

        // Password
        passLabel = new JLabel("Password:");
        passLabel.setFont(UITheme.FONT_LABEL);
        passLabel.setForeground(UITheme.TEXT_DARK);
        passLabel.setBounds(40, 112, 180, 20);
        bodyPanel.add(passLabel);

        passField = UITheme.createPasswordField();
        passField.setBounds(40, 135, 385, 34);
        bodyPanel.add(passField);

        // Show Password Checkbox
        showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        showPasswordCheck.setOpaque(false);
        showPasswordCheck.setForeground(UITheme.TEXT_MUTED);
        showPasswordCheck.setBounds(40, 172, 140, 20);
        showPasswordCheck.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                passField.setEchoChar((char) 0);
            } else {
                passField.setEchoChar('•');
            }
        });
        bodyPanel.add(showPasswordCheck);

        // ---- Buttons ----
        loginButton = UITheme.createPrimaryButton("Student Login");
        loginButton.setBounds(40, 205, 185, 36);
        loginButton.addActionListener(this);
        bodyPanel.add(loginButton);

        adminLoginButton = UITheme.createDarkButton("Admin Login");
        adminLoginButton.setBounds(240, 205, 185, 36);
        adminLoginButton.addActionListener(this);
        bodyPanel.add(adminLoginButton);

        registerButton = UITheme.createSecondaryButton("New Student? Create an Account");
        registerButton.setBounds(40, 252, 385, 34);
        registerButton.addActionListener(this);
        bodyPanel.add(registerButton);

        add(bodyPanel, BorderLayout.CENTER);

        // Set default action button on Enter key
        getRootPane().setDefaultButton(loginButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            handleStudentLogin();
        } else if (e.getSource() == adminLoginButton) {
            handleAdminLogin();
        } else if (e.getSource() == registerButton) {
            new Register();
            dispose();
        }
    }

    /**
     * Authenticates student credentials against the MySQL `users` table.
     */
    private void handleStudentLogin() {
        String identifier = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (identifier.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both your Username/Email and Password.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT id, full_name, username, email, role FROM users WHERE (username = ? OR email = ?) AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, identifier);
            pstmt.setString(2, identifier);
            pstmt.setString(3, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    String fullName = rs.getString("full_name");
                    String role = rs.getString("role");

                    JOptionPane.showMessageDialog(this,
                            "Welcome back, " + fullName + "!\nLogin successful as " + role.toUpperCase() + ".",
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);

                    new StudentDashboard(username, fullName);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid Username/Email or Password.\nPlease check your credentials and try again.",
                            "Authentication Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error while authenticating:\n" + ex.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Authenticates admin credentials and ensures the user possesses the 'admin' role.
     */
    private void handleAdminLogin() {
        String identifier = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (identifier.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter Admin Username and Password.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT id, full_name, username, email, role FROM users WHERE (username = ? OR email = ?) AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, identifier);
            pstmt.setString(2, identifier);
            pstmt.setString(3, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    String fullName = rs.getString("full_name");

                    if ("admin".equalsIgnoreCase(role)) {
                        JOptionPane.showMessageDialog(this,
                                "Administrator authentication successful.\nWelcome, " + fullName + "!",
                                "Admin Login Successful",
                                JOptionPane.INFORMATION_MESSAGE);

                        new AdminDashboard(fullName);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Access Denied: Account '" + identifier + "' does not have Administrator privileges.\nPlease use Student Login.",
                                "Authorization Error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid Admin Username/Email or Password.",
                            "Authentication Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error while authenticating admin:\n" + ex.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

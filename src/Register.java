/*
 * Student Registration Page
 * Project: National Scholarship Portal (NSP)
 * Made by: Kapil Tamoli
 * Upgraded: End-to-End MySQL JDBC Integration & Modern UI
 *
 * Validates inputs, ensures username/email uniqueness, and registers
 * new students into the `users` table.
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
import java.util.regex.Pattern;

public class Register extends JFrame implements ActionListener {

    JLabel nameLabel, emailLabel, phoneLabel, userLabel, passLabel;
    JTextField nameField, emailField, phoneField, userField;
    JPasswordField passField;
    JCheckBox showPasswordCheck;
    JButton registerButton, backButton;

    // Email validation regex pattern
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    public Register() {
        UITheme.applySystemLookAndFeel();

        setTitle("NSP - Student Registration");
        setSize(480, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_LIGHT);

        // ---- Header Panel ----
        JPanel headerPanel = UITheme.createHeaderPanel(
                "Student Registration",
                "Create a student account to apply for government scholarships",
                "NEW ACCOUNT"
        );
        add(headerPanel, BorderLayout.NORTH);

        // ---- Form Body Panel ----
        JPanel bodyPanel = new JPanel(null);
        bodyPanel.setBackground(UITheme.BG_LIGHT);
        bodyPanel.setBorder(new EmptyBorder(10, 25, 15, 25));

        int labelX = 40;
        int fieldX = 40;
        int fieldWidth = 385;
        int fieldHeight = 32;

        // 1. Full Name
        nameLabel = new JLabel("Full Name (as per official ID):");
        nameLabel.setFont(UITheme.FONT_LABEL);
        nameLabel.setForeground(UITheme.TEXT_DARK);
        nameLabel.setBounds(labelX, 15, 300, 20);
        bodyPanel.add(nameLabel);

        nameField = UITheme.createTextField();
        nameField.setBounds(fieldX, 35, fieldWidth, fieldHeight);
        bodyPanel.add(nameField);

        // 2. Email Address
        emailLabel = new JLabel("Email Address:");
        emailLabel.setFont(UITheme.FONT_LABEL);
        emailLabel.setForeground(UITheme.TEXT_DARK);
        emailLabel.setBounds(labelX, 75, 300, 20);
        bodyPanel.add(emailLabel);

        emailField = UITheme.createTextField();
        emailField.setBounds(fieldX, 95, fieldWidth, fieldHeight);
        bodyPanel.add(emailField);

        // 3. Phone Number
        phoneLabel = new JLabel("Mobile Number (10 digits):");
        phoneLabel.setFont(UITheme.FONT_LABEL);
        phoneLabel.setForeground(UITheme.TEXT_DARK);
        phoneLabel.setBounds(labelX, 135, 300, 20);
        bodyPanel.add(phoneLabel);

        phoneField = UITheme.createTextField();
        phoneField.setBounds(fieldX, 155, fieldWidth, fieldHeight);
        bodyPanel.add(phoneField);

        // 4. Choose Username
        userLabel = new JLabel("Choose Username:");
        userLabel.setFont(UITheme.FONT_LABEL);
        userLabel.setForeground(UITheme.TEXT_DARK);
        userLabel.setBounds(labelX, 195, 300, 20);
        bodyPanel.add(userLabel);

        userField = UITheme.createTextField();
        userField.setBounds(fieldX, 215, fieldWidth, fieldHeight);
        bodyPanel.add(userField);

        // 5. Choose Password
        passLabel = new JLabel("Choose Password:");
        passLabel.setFont(UITheme.FONT_LABEL);
        passLabel.setForeground(UITheme.TEXT_DARK);
        passLabel.setBounds(labelX, 255, 300, 20);
        bodyPanel.add(passLabel);

        passField = UITheme.createPasswordField();
        passField.setBounds(fieldX, 275, fieldWidth, fieldHeight);
        bodyPanel.add(passField);

        // Show Password Checkbox
        showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        showPasswordCheck.setOpaque(false);
        showPasswordCheck.setForeground(UITheme.TEXT_MUTED);
        showPasswordCheck.setBounds(fieldX, 310, 140, 20);
        showPasswordCheck.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                passField.setEchoChar((char) 0);
            } else {
                passField.setEchoChar('•');
            }
        });
        bodyPanel.add(showPasswordCheck);

        // Buttons
        registerButton = UITheme.createSuccessButton("Complete Registration");
        registerButton.setBounds(fieldX, 340, 220, 36);
        registerButton.addActionListener(this);
        bodyPanel.add(registerButton);

        backButton = UITheme.createSecondaryButton("Back to Login");
        backButton.setBounds(270, 340, 155, 36);
        backButton.addActionListener(this);
        bodyPanel.add(backButton);

        add(bodyPanel, BorderLayout.CENTER);

        getRootPane().setDefaultButton(registerButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            handleRegistration();
        } else if (e.getSource() == backButton) {
            new Login();
            dispose();
        }
    }

    /**
     * Performs end-to-end form validation, duplicate checking, and database insertion.
     */
    private void handleRegistration() {
        String fullName = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        // 1. Mandatory Fields Check
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all registration fields.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Email Format Validation
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address (e.g., student@example.com).",
                    "Invalid Email",
                    JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return;
        }

        // 3. Phone Number Validation
        if (!phone.matches("\\d{10,15}")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid phone number (10 to 15 digits).",
                    "Invalid Phone Number",
                    JOptionPane.WARNING_MESSAGE);
            phoneField.requestFocus();
            return;
        }

        // 4. Password Minimum Length
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 4 characters long.",
                    "Weak Password",
                    JOptionPane.WARNING_MESSAGE);
            passField.requestFocus();
            return;
        }

        // 5. Database Checks & Insertion
        try (Connection conn = DBConnection.getConnection()) {

            // Check if username already exists
            String checkUserSql = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkUserSql)) {
                checkStmt.setString(1, username);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this,
                                "Username '" + username + "' is already taken.\nPlease choose a different username.",
                                "Duplicate Username",
                                JOptionPane.WARNING_MESSAGE);
                        userField.requestFocus();
                        return;
                    }
                }
            }

            // Check if email already exists
            String checkEmailSql = "SELECT id FROM users WHERE email = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkEmailSql)) {
                checkStmt.setString(1, email);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this,
                                "An account with email '" + email + "' already exists.\nPlease sign in instead.",
                                "Duplicate Email",
                                JOptionPane.WARNING_MESSAGE);
                        emailField.requestFocus();
                        return;
                    }
                }
            }

            // Insert new student record
            String insertSql = "INSERT INTO users (full_name, email, phone, username, password, role) VALUES (?, ?, ?, ?, ?, 'student')";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, fullName);
                insertStmt.setString(2, email);
                insertStmt.setString(3, phone);
                insertStmt.setString(4, username);
                insertStmt.setString(5, password);

                int rowsAffected = insertStmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Registration successful!\nYour student account has been created. Please log in.",
                            "Account Created",
                            JOptionPane.INFORMATION_MESSAGE);

                    new Login();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to register. Please try again.",
                            "Registration Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error during registration:\n" + ex.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

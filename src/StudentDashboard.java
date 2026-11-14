/*
 * Student Dashboard (Home Page after Student Login)
 * Project: National Scholarship Portal (NSP)
 * Made by: Kapil Tamoli
 * Upgraded: End-to-End MySQL JDBC Integration & Modern UI
 *
 * Displays personalized student welcome, real-time application count & status,
 * and quick navigation to apply or view status.
 */

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDashboard extends JFrame implements ActionListener {

    private final String username;
    private final String fullName;

    private JLabel welcomeLabel;
    private JLabel totalAppsLabel, latestStatusLabel;
    private JButton applyButton, statusButton, logoutButton;

    public StudentDashboard() {
        this("student", "Student");
    }

    public StudentDashboard(String username) {
        this(username, fetchFullName(username));
    }

    public StudentDashboard(String username, String fullName) {
        this.username = (username != null && !username.trim().isEmpty()) ? username : "student";
        this.fullName = (fullName != null && !fullName.trim().isEmpty()) ? fullName : this.username;

        UITheme.applySystemLookAndFeel();

        setTitle("NSP - Student Dashboard (" + this.username + ")");
        setSize(520, 440);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_LIGHT);

        // ---- Header Panel ----
        JPanel headerPanel = UITheme.createHeaderPanel(
                "Student Dashboard",
                "National Scholarship Portal | Applicant Services",
                "STUDENT: " + this.username.toUpperCase()
        );
        add(headerPanel, BorderLayout.NORTH);

        // ---- Body Panel ----
        JPanel bodyPanel = new JPanel(null);
        bodyPanel.setBackground(UITheme.BG_LIGHT);
        bodyPanel.setBorder(new EmptyBorder(15, 25, 20, 25));

        // Welcome Title
        welcomeLabel = new JLabel("Welcome, " + this.fullName + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        welcomeLabel.setForeground(UITheme.TEXT_DARK);
        welcomeLabel.setBounds(40, 15, 420, 25);
        bodyPanel.add(welcomeLabel);

        JLabel subNote = new JLabel("Manage your scholarship applications and track approval statuses in real time.");
        subNote.setFont(UITheme.FONT_SUBTITLE);
        subNote.setForeground(UITheme.TEXT_MUTED);
        subNote.setBounds(40, 40, 440, 20);
        bodyPanel.add(subNote);

        // Quick Stats Cards Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBounds(40, 70, 425, 75);

        // Card 1: Total Applied
        JPanel card1 = createStatCard("Your Applications", "Loading...", UITheme.ACCENT);
        totalAppsLabel = (JLabel) card1.getComponent(1);
        statsPanel.add(card1);

        // Card 2: Latest Status
        JPanel card2 = createStatCard("Latest Status", "Loading...", UITheme.PRIMARY);
        latestStatusLabel = (JLabel) card2.getComponent(1);
        statsPanel.add(card2);

        bodyPanel.add(statsPanel);

        // Navigation Action Buttons
        applyButton = UITheme.createPrimaryButton("Apply for New Scholarship");
        applyButton.setBounds(70, 175, 365, 42);
        applyButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        applyButton.addActionListener(this);
        bodyPanel.add(applyButton);

        statusButton = UITheme.createSecondaryButton("Track Application Status");
        statusButton.setBounds(70, 230, 365, 40);
        statusButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusButton.addActionListener(this);
        bodyPanel.add(statusButton);

        logoutButton = UITheme.createDangerButton("Logout from Portal");
        logoutButton.setBounds(70, 285, 365, 38);
        logoutButton.addActionListener(this);
        bodyPanel.add(logoutButton);

        add(bodyPanel, BorderLayout.CENTER);

        // Fetch live metrics from database
        loadStudentMetrics();

        setVisible(true);
    }

    private JPanel createStatCard(String title, String initialValue, Color accentColor) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(UITheme.TEXT_MUTED);
        card.add(titleLbl);

        JLabel valLbl = new JLabel(initialValue);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valLbl.setForeground(accentColor);
        card.add(valLbl);

        return card;
    }

    /**
     * Queries database to show real-time application count and latest status.
     */
    private void loadStudentMetrics() {
        int count = 0;
        String latestStatus = "No Applications";

        String countSql = "SELECT COUNT(*) FROM applications WHERE username = ? OR student_name = ?";
        String latestSql = "SELECT status FROM applications WHERE username = ? OR student_name = ? ORDER BY id DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(countSql)) {
                stmt.setString(1, username);
                stmt.setString(2, fullName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(latestSql)) {
                stmt.setString(1, username);
                stmt.setString(2, fullName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        latestStatus = rs.getString("status");
                    }
                }
            }

            totalAppsLabel.setText(count + " Submitted");
            latestStatusLabel.setText(latestStatus);

            if ("Approved".equalsIgnoreCase(latestStatus)) {
                latestStatusLabel.setForeground(UITheme.SUCCESS);
            } else if ("Rejected".equalsIgnoreCase(latestStatus)) {
                latestStatusLabel.setForeground(UITheme.DANGER);
            } else if ("Pending".equalsIgnoreCase(latestStatus)) {
                latestStatusLabel.setForeground(UITheme.WARNING);
            }

        } catch (SQLException e) {
            totalAppsLabel.setText("Offline");
            latestStatusLabel.setText("Database Unavailable");
        }
    }

    private static String fetchFullName(String user) {
        if (user == null || user.trim().isEmpty()) return "Student";
        String sql = "SELECT full_name FROM users WHERE username = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("full_name");
                }
            }
        } catch (Exception ignored) {
        }
        return user;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == applyButton) {
            new ApplyScholarship(username, fullName);
            dispose();
        } else if (e.getSource() == statusButton) {
            new ViewStatus(username, fullName);
            dispose();
        } else if (e.getSource() == logoutButton) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new Login();
                dispose();
            }
        }
    }
}

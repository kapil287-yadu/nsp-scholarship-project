/*
 * Admin Dashboard
 * Project: National Scholarship Portal (NSP)
 * Made by: Kapil Tamoli
 * Upgraded: End-to-End MySQL JDBC Integration & Modern UI
 *
 * Provides high-level administrative KPI metrics (Total, Pending, Approved, Rejected)
 * and navigation to review and manage all student applications.
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

public class AdminDashboard extends JFrame implements ActionListener {

    private final String adminName;

    private JLabel titleLabel;
    private JLabel totalCountLabel, pendingCountLabel, approvedCountLabel, rejectedCountLabel;
    private JButton viewAppsButton, refreshButton, logoutButton;

    public AdminDashboard() {
        this("Admin");
    }

    public AdminDashboard(String adminName) {
        this.adminName = (adminName != null && !adminName.trim().isEmpty()) ? adminName : "Admin";

        UITheme.applySystemLookAndFeel();

        setTitle("NSP - Administrator Control Center");
        setSize(560, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_LIGHT);

        // ---- Header Panel ----
        JPanel headerPanel = UITheme.createHeaderPanel(
                "Admin Control Center",
                "National Scholarship Portal | Portal Administration",
                "ADMIN: " + this.adminName.toUpperCase()
        );
        add(headerPanel, BorderLayout.NORTH);

        // ---- Body Panel ----
        JPanel bodyPanel = new JPanel(null);
        bodyPanel.setBackground(UITheme.BG_LIGHT);
        bodyPanel.setBorder(new EmptyBorder(15, 25, 20, 25));

        // Welcome Title
        titleLabel = new JLabel("Welcome back, " + this.adminName + "!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(UITheme.TEXT_DARK);
        titleLabel.setBounds(40, 15, 450, 25);
        bodyPanel.add(titleLabel);

        JLabel subNote = new JLabel("Real-time summary of scholarship submissions across all departments:");
        subNote.setFont(UITheme.FONT_SUBTITLE);
        subNote.setForeground(UITheme.TEXT_MUTED);
        subNote.setBounds(40, 40, 460, 20);
        bodyPanel.add(subNote);

        // 2x2 Grid of Metrics Cards
        JPanel metricsGrid = new JPanel(new GridLayout(2, 2, 12, 12));
        metricsGrid.setOpaque(false);
        metricsGrid.setBounds(40, 70, 465, 160);

        // Card 1: Total Applications
        JPanel cardTotal = createStatCard("Total Applications", "0", UITheme.PRIMARY);
        totalCountLabel = (JLabel) cardTotal.getComponent(1);
        metricsGrid.add(cardTotal);

        // Card 2: Pending Applications
        JPanel cardPending = createStatCard("Pending Verification", "0", UITheme.WARNING);
        pendingCountLabel = (JLabel) cardPending.getComponent(1);
        metricsGrid.add(cardPending);

        // Card 3: Approved Applications
        JPanel cardApproved = createStatCard("Approved Applications", "0", UITheme.SUCCESS);
        approvedCountLabel = (JLabel) cardApproved.getComponent(1);
        metricsGrid.add(cardApproved);

        // Card 4: Rejected Applications
        JPanel cardRejected = createStatCard("Rejected Applications", "0", UITheme.DANGER);
        rejectedCountLabel = (JLabel) cardRejected.getComponent(1);
        metricsGrid.add(cardRejected);

        bodyPanel.add(metricsGrid);

        // Action Buttons
        viewAppsButton = UITheme.createPrimaryButton("Review All Applications & Approve/Reject");
        viewAppsButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewAppsButton.setBounds(40, 250, 465, 42);
        viewAppsButton.addActionListener(this);
        bodyPanel.add(viewAppsButton);

        refreshButton = UITheme.createSecondaryButton("Refresh Statistics");
        refreshButton.setBounds(40, 305, 225, 36);
        refreshButton.addActionListener(this);
        bodyPanel.add(refreshButton);

        logoutButton = UITheme.createDangerButton("Logout from Portal");
        logoutButton.setBounds(280, 305, 225, 36);
        logoutButton.addActionListener(this);
        bodyPanel.add(logoutButton);

        add(bodyPanel, BorderLayout.CENTER);

        // Fetch live statistics
        loadDashboardStats();

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
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valLbl.setForeground(accentColor);
        card.add(valLbl);

        return card;
    }

    /**
     * Queries database to aggregate real-time counts for applications.
     */
    private void loadDashboardStats() {
        int total = 0, pending = 0, approved = 0, rejected = 0;

        String sql = "SELECT status, COUNT(*) AS cnt FROM applications GROUP BY status";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("cnt");
                total += count;

                if ("Pending".equalsIgnoreCase(status)) {
                    pending += count;
                } else if ("Approved".equalsIgnoreCase(status)) {
                    approved += count;
                } else if ("Rejected".equalsIgnoreCase(status)) {
                    rejected += count;
                }
            }

            totalCountLabel.setText(String.valueOf(total));
            pendingCountLabel.setText(String.valueOf(pending));
            approvedCountLabel.setText(String.valueOf(approved));
            rejectedCountLabel.setText(String.valueOf(rejected));

        } catch (SQLException e) {
            totalCountLabel.setText("N/A");
            pendingCountLabel.setText("N/A");
            approvedCountLabel.setText("N/A");
            rejectedCountLabel.setText("N/A");
        }
    }

    public String getAdminName() {
        return adminName;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewAppsButton) {
            new ViewApplications(adminName);
            dispose();
        } else if (e.getSource() == refreshButton) {
            loadDashboardStats();
            JOptionPane.showMessageDialog(this,
                    "Admin statistics updated from MySQL database.",
                    "Refreshed",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == logoutButton) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to log out from the Administrator portal?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new Login();
                dispose();
            }
        }
    }
}

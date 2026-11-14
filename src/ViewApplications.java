/*
 * View All Applications Page (Admin Side)
 * Project: National Scholarship Portal (NSP)
 * Made by: Kapil Tamoli
 * Upgraded: End-to-End MySQL JDBC Integration & Modern UI
 *
 * Displays all student scholarship submissions from MySQL `applications` table
 * and provides real-time "Approve", "Reject", and "Reset to Pending" controls.
 */

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class ViewApplications extends JFrame implements ActionListener {

    private final String adminName;

    private JTable appTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterStatusBox;
    private JTextField searchField;
    private JLabel countLabel, selectedAppLabel;
    private JButton approveButton, rejectButton, resetPendingButton, refreshButton, backButton;

    public ViewApplications() {
        this("Admin");
    }

    public ViewApplications(String adminName) {
        this.adminName = (adminName != null && !adminName.trim().isEmpty()) ? adminName : "Admin";

        UITheme.applySystemLookAndFeel();

        setTitle("NSP - Scholarship Applications Management");
        setSize(940, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_LIGHT);

        // ---- Header Panel ----
        JPanel headerPanel = UITheme.createHeaderPanel(
                "Scholarship Applications Management",
                "Review, verify, and approve or reject student scholarship submissions",
                "ADMIN: " + this.adminName.toUpperCase()
        );
        add(headerPanel, BorderLayout.NORTH);

        // ---- Center Content Panel ----
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(UITheme.BG_LIGHT);
        contentPanel.setBorder(new EmptyBorder(12, 18, 12, 18));

        // Filter & Search Toolbar
        JPanel toolbarPanel = new JPanel(new BorderLayout(10, 0));
        toolbarPanel.setOpaque(false);

        // Left toolbar: Filter by Status
        JPanel leftTool = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        leftTool.setOpaque(false);

        JLabel filterLbl = new JLabel("Filter Status:");
        filterLbl.setFont(UITheme.FONT_LABEL);
        filterLbl.setForeground(UITheme.TEXT_DARK);
        leftTool.add(filterLbl);

        String[] filterOptions = {"All Applications", "Pending Only", "Approved Only", "Rejected Only"};
        filterStatusBox = UITheme.createComboBox(filterOptions);
        filterStatusBox.setPreferredSize(new Dimension(150, 30));
        filterStatusBox.addActionListener(e -> loadApplicationsFromDB());
        leftTool.add(filterStatusBox);

        toolbarPanel.add(leftTool, BorderLayout.WEST);

        // Right toolbar: Live Search Field
        JPanel rightTool = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        rightTool.setOpaque(false);

        JLabel searchLbl = new JLabel("Search (Name/Course):");
        searchLbl.setFont(UITheme.FONT_LABEL);
        searchLbl.setForeground(UITheme.TEXT_DARK);
        rightTool.add(searchLbl);

        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(180, 30));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadApplicationsFromDB();
            }
        });
        rightTool.add(searchField);

        refreshButton = UITheme.createSecondaryButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setPreferredSize(new Dimension(85, 30));
        refreshButton.addActionListener(this);
        rightTool.add(refreshButton);

        toolbarPanel.add(rightTool, BorderLayout.EAST);

        contentPanel.add(toolbarPanel, BorderLayout.NORTH);

        // Table Model & JTable
        String[] columnNames = {"ID", "App ID", "Student Name", "Username", "Course", "Class/Year", "Category", "Income (₹)", "Status", "Applied Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only cells
            }
        };

        appTable = new JTable(tableModel);
        UITheme.styleTable(appTable);

        // Hide raw database ID column from view while keeping it in the model
        appTable.getColumnModel().getColumn(0).setMinWidth(0);
        appTable.getColumnModel().getColumn(0).setMaxWidth(0);
        appTable.getColumnModel().getColumn(0).setWidth(0);

        // Set column widths
        appTable.getColumnModel().getColumn(1).setPreferredWidth(75);   // App ID
        appTable.getColumnModel().getColumn(2).setPreferredWidth(140);  // Student Name
        appTable.getColumnModel().getColumn(3).setPreferredWidth(85);   // Username
        appTable.getColumnModel().getColumn(4).setPreferredWidth(130);  // Course
        appTable.getColumnModel().getColumn(5).setPreferredWidth(85);   // Class/Year
        appTable.getColumnModel().getColumn(6).setPreferredWidth(75);   // Category
        appTable.getColumnModel().getColumn(7).setPreferredWidth(95);   // Income
        appTable.getColumnModel().getColumn(8).setPreferredWidth(90);   // Status
        appTable.getColumnModel().getColumn(9).setPreferredWidth(115);  // Date

        // Row selection listener to update the action bar
        appTable.getSelectionModel().addListSelectionListener(e -> updateSelectionDetails());

        JScrollPane scrollPane = new JScrollPane(appTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // ---- Bottom Action & Management Panel ----
        JPanel bottomContainer = new JPanel(new BorderLayout(5, 5));
        bottomContainer.setOpaque(false);
        bottomContainer.setBorder(new EmptyBorder(8, 0, 0, 0));

        // Sub-bar 1: Status & Selected item banner
        JPanel infoBar = new JPanel(new BorderLayout());
        infoBar.setOpaque(false);

        countLabel = new JLabel("Total: 0 application(s)");
        countLabel.setFont(UITheme.FONT_LABEL);
        countLabel.setForeground(UITheme.TEXT_MUTED);
        infoBar.add(countLabel, BorderLayout.WEST);

        selectedAppLabel = new JLabel("Click any application row above to Approve or Reject.");
        selectedAppLabel.setFont(UITheme.FONT_LABEL);
        selectedAppLabel.setForeground(UITheme.ACCENT);
        infoBar.add(selectedAppLabel, BorderLayout.EAST);

        bottomContainer.add(infoBar, BorderLayout.NORTH);

        // Sub-bar 2: Action Buttons
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        actionButtonPanel.setOpaque(false);

        approveButton = UITheme.createSuccessButton("Approve Selected");
        approveButton.addActionListener(this);
        actionButtonPanel.add(approveButton);

        rejectButton = UITheme.createDangerButton("Reject Selected");
        rejectButton.addActionListener(this);
        actionButtonPanel.add(rejectButton);

        resetPendingButton = UITheme.createSecondaryButton("Reset to Pending");
        resetPendingButton.addActionListener(this);
        actionButtonPanel.add(resetPendingButton);

        backButton = UITheme.createSecondaryButton("Back to Dashboard");
        backButton.addActionListener(this);
        actionButtonPanel.add(backButton);

        bottomContainer.add(actionButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(bottomContainer, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        // Load data from MySQL database
        loadApplicationsFromDB();

        setVisible(true);
    }

    /**
     * Updates the preview label when admin selects a row in the table.
     */
    private void updateSelectionDetails() {
        int selectedRow = appTable.getSelectedRow();
        if (selectedRow != -1) {
            String appId = (String) tableModel.getValueAt(selectedRow, 1);
            String studentName = (String) tableModel.getValueAt(selectedRow, 2);
            String course = (String) tableModel.getValueAt(selectedRow, 4);
            String status = (String) tableModel.getValueAt(selectedRow, 8);

            selectedAppLabel.setText("Selected: " + appId + " | " + studentName + " (" + course + ") | Status: " + status.toUpperCase());
        } else {
            selectedAppLabel.setText("Click any application row above to Approve or Reject.");
        }
    }

    /**
     * Queries database to populate applications table with optional status filtering and live search.
     */
    private void loadApplicationsFromDB() {
        tableModel.setRowCount(0);

        String filterChoice = (String) filterStatusBox.getSelectedItem();
        String keyword = searchField.getText().trim();

        StringBuilder sql = new StringBuilder("SELECT id, student_name, username, course_name, class_year, category, family_income, status, applied_date FROM applications WHERE 1=1 ");

        if ("Pending Only".equals(filterChoice)) {
            sql.append(" AND status = 'Pending'");
        } else if ("Approved Only".equals(filterChoice)) {
            sql.append(" AND status = 'Approved'");
        } else if ("Rejected Only".equals(filterChoice)) {
            sql.append(" AND status = 'Rejected'");
        }

        if (!keyword.isEmpty()) {
            sql.append(" AND (student_name LIKE ? OR course_name LIKE ? OR username LIKE ?)");
        }

        sql.append(" ORDER BY id DESC");

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        int count = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            if (!keyword.isEmpty()) {
                String searchPattern = "%" + keyword + "%";
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                pstmt.setString(3, searchPattern);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    count++;
                    int rawId = rs.getInt("id");
                    String studentName = rs.getString("student_name");
                    String username = rs.getString("username");
                    String course = rs.getString("course_name");
                    String classYear = rs.getString("class_year");
                    String category = rs.getString("category");
                    double income = rs.getDouble("family_income");
                    String status = rs.getString("status");
                    java.sql.Timestamp appliedDate = rs.getTimestamp("applied_date");

                    String dateStr = (appliedDate != null)
                            ? appliedDate.toString().substring(0, 16)
                            : "N/A";

                    tableModel.addRow(new Object[]{
                            rawId,
                            "NSP" + String.format("%03d", rawId),
                            studentName,
                            username,
                            course,
                            classYear,
                            category,
                            currencyFormat.format(income),
                            status,
                            dateStr
                    });
                }
            }

            countLabel.setText("Showing " + count + " application(s)");
            updateSelectionDetails();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error while retrieving applications:\n" + ex.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Executes real-time status update in MySQL (`UPDATE applications SET status = ? WHERE id = ?`).
     */
    private void updateApplicationStatus(String newStatus) {
        int selectedRow = appTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an application row from the table first.",
                    "No Application Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int rawId = (int) tableModel.getValueAt(selectedRow, 0);
        String appId = (String) tableModel.getValueAt(selectedRow, 1);
        String studentName = (String) tableModel.getValueAt(selectedRow, 2);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 8);

        if (newStatus.equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this,
                    "Application " + appId + " is already marked as " + newStatus.toUpperCase() + ".",
                    "Status Unchanged",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to mark application " + appId + " for " + studentName + " as " + newStatus.toUpperCase() + "?",
                "Confirm Status Change",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "UPDATE applications SET status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, rawId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this,
                        "Application " + appId + " has been successfully updated to " + newStatus.toUpperCase() + ".",
                        "Status Updated",
                        JOptionPane.INFORMATION_MESSAGE);

                loadApplicationsFromDB();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not update application status. Record may no longer exist.",
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error while updating status:\n" + ex.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == approveButton) {
            updateApplicationStatus("Approved");
        } else if (e.getSource() == rejectButton) {
            updateApplicationStatus("Rejected");
        } else if (e.getSource() == resetPendingButton) {
            updateApplicationStatus("Pending");
        } else if (e.getSource() == refreshButton) {
            loadApplicationsFromDB();
        } else if (e.getSource() == backButton) {
            new AdminDashboard(adminName);
            dispose();
        }
    }
}

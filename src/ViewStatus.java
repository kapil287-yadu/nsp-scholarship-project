/*
 * View Application Status Page
 * Project: National Scholarship Portal (NSP)
 * Made by: Kapil Tamoli
 * Upgraded: End-to-End MySQL JDBC Integration & Modern UI
 *
 * Fetches and displays real-time scholarship application records
 * from MySQL `applications` table for the logged-in student.
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class ViewStatus extends JFrame implements ActionListener {

    private final String username;
    private final String studentName;

    private JTable statusTable;
    private DefaultTableModel tableModel;
    private JLabel infoLabel;
    private JButton refreshButton, applyNewButton, backButton;

    public ViewStatus() {
        this("student", "Student");
    }

    public ViewStatus(String username, String studentName) {
        this.username = (username != null && !username.trim().isEmpty()) ? username : "student";
        this.studentName = (studentName != null && !studentName.trim().isEmpty()) ? studentName : this.username;

        UITheme.applySystemLookAndFeel();

        setTitle("NSP - My Scholarship Applications");
        setSize(780, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_LIGHT);

        // ---- Header Panel ----
        JPanel headerPanel = UITheme.createHeaderPanel(
                "My Scholarship Applications",
                "Real-time application verification and approval status",
                "STUDENT: " + this.username.toUpperCase()
        );
        add(headerPanel, BorderLayout.NORTH);

        // ---- Center Content Panel ----
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(UITheme.BG_LIGHT);
        contentPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Info Banner
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        infoLabel = new JLabel("Applications registered for " + this.studentName + " (" + this.username + "):");
        infoLabel.setFont(UITheme.FONT_LABEL);
        infoLabel.setForeground(UITheme.TEXT_DARK);
        topBar.add(infoLabel, BorderLayout.WEST);

        refreshButton = UITheme.createSecondaryButton("Refresh Status");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.addActionListener(this);
        topBar.add(refreshButton, BorderLayout.EAST);

        contentPanel.add(topBar, BorderLayout.NORTH);

        // Table Model & JTable
        String[] columnNames = {"App ID", "Course", "Class/Year", "Category", "Family Income", "Status", "Applied Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table cells are view-only
            }
        };

        statusTable = new JTable(tableModel);
        UITheme.styleTable(statusTable);

        // Set column widths
        statusTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        statusTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        statusTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        statusTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        statusTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        statusTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        statusTable.getColumnModel().getColumn(6).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(statusTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // ---- Bottom Action Buttons Bar ----
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottomBar.setBackground(UITheme.BG_LIGHT);

        applyNewButton = UITheme.createPrimaryButton("+ Apply for Another Scholarship");
        applyNewButton.addActionListener(this);
        bottomBar.add(applyNewButton);

        backButton = UITheme.createSecondaryButton("Back to Dashboard");
        backButton.addActionListener(this);
        bottomBar.add(backButton);

        contentPanel.add(bottomBar, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        // Fetch real-time applications from MySQL
        loadApplicationsFromDB();

        setVisible(true);
    }

    /**
     * Executes a SELECT query to retrieve scholarship applications for the student.
     */
    private void loadApplicationsFromDB() {
        tableModel.setRowCount(0); // clear existing rows

        String sql = "SELECT id, course_name, class_year, category, family_income, status, applied_date "
                   + "FROM applications WHERE username = ? OR student_name = ? ORDER BY id DESC";

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        int recordCount = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, this.username);
            pstmt.setString(2, this.studentName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    recordCount++;
                    int id = rs.getInt("id");
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
                            "NSP" + String.format("%03d", id),
                            course,
                            classYear,
                            category,
                            currencyFormat.format(income),
                            status,
                            dateStr
                    });
                }
            }

            if (recordCount == 0) {
                infoLabel.setText("No applications found for " + this.studentName + ". Click '+ Apply for Another Scholarship' to submit.");
            } else {
                infoLabel.setText("Showing " + recordCount + " scholarship application(s) for " + this.studentName + ":");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error while fetching application status:\n" + ex.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refreshButton) {
            loadApplicationsFromDB();
        } else if (e.getSource() == applyNewButton) {
            new ApplyScholarship(username, studentName);
            dispose();
        } else if (e.getSource() == backButton) {
            new StudentDashboard(username, studentName);
            dispose();
        }
    }
}

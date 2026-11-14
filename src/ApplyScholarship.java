/*
 * Apply Scholarship Page
 * Project: National Scholarship Portal (NSP)
 * Made by: Kapil Tamoli
 * Upgraded: End-to-End MySQL JDBC Integration & Modern UI
 *
 * Collects scholarship application details, validates numeric family income,
 * and records the application into the `applications` table with status 'Pending'.
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ApplyScholarship extends JFrame implements ActionListener {

    private final String username;
    private final String studentName;

    JLabel nameLabel, classLabel, categoryLabel, incomeLabel, courseLabel;
    JTextField nameField, classField, incomeField, courseField;
    JComboBox<String> categoryBox;
    JButton submitButton, backButton;

    public ApplyScholarship() {
        this("student", "Student");
    }

    public ApplyScholarship(String username, String studentName) {
        this.username = (username != null && !username.trim().isEmpty()) ? username : "student";
        this.studentName = (studentName != null && !studentName.trim().isEmpty()) ? studentName : this.username;

        UITheme.applySystemLookAndFeel();

        setTitle("NSP - Apply for Scholarship");
        setSize(500, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_LIGHT);

        // ---- Header Panel ----
        JPanel headerPanel = UITheme.createHeaderPanel(
                "Scholarship Application Form",
                "Apply for National Central Sector & Merit Scholarships",
                "APPLICANT: " + this.username.toUpperCase()
        );
        add(headerPanel, BorderLayout.NORTH);

        // ---- Form Body Panel ----
        JPanel bodyPanel = new JPanel(null);
        bodyPanel.setBackground(UITheme.BG_LIGHT);
        bodyPanel.setBorder(new EmptyBorder(10, 25, 15, 25));

        int labelX = 40;
        int fieldX = 40;
        int fieldWidth = 405;
        int fieldHeight = 32;

        // 1. Student Name
        nameLabel = new JLabel("Student Full Name:");
        nameLabel.setFont(UITheme.FONT_LABEL);
        nameLabel.setForeground(UITheme.TEXT_DARK);
        nameLabel.setBounds(labelX, 15, 300, 20);
        bodyPanel.add(nameLabel);

        nameField = UITheme.createTextField();
        nameField.setText(this.studentName);
        nameField.setBounds(fieldX, 35, fieldWidth, fieldHeight);
        bodyPanel.add(nameField);

        // 2. Class / Academic Year
        classLabel = new JLabel("Current Class / Year (e.g., 1st Year, 2nd Year, 3rd Year):");
        classLabel.setFont(UITheme.FONT_LABEL);
        classLabel.setForeground(UITheme.TEXT_DARK);
        classLabel.setBounds(labelX, 75, 400, 20);
        bodyPanel.add(classLabel);

        classField = UITheme.createTextField();
        classField.setBounds(fieldX, 95, fieldWidth, fieldHeight);
        bodyPanel.add(classField);

        // 3. Course Name
        courseLabel = new JLabel("Course / Degree (e.g., B.Tech CSE, B.Sc, BCA):");
        courseLabel.setFont(UITheme.FONT_LABEL);
        courseLabel.setForeground(UITheme.TEXT_DARK);
        courseLabel.setBounds(labelX, 135, 400, 20);
        bodyPanel.add(courseLabel);

        courseField = UITheme.createTextField();
        courseField.setBounds(fieldX, 155, fieldWidth, fieldHeight);
        bodyPanel.add(courseField);

        // 4. Category
        categoryLabel = new JLabel("Reservation Category:");
        categoryLabel.setFont(UITheme.FONT_LABEL);
        categoryLabel.setForeground(UITheme.TEXT_DARK);
        categoryLabel.setBounds(labelX, 195, 300, 20);
        bodyPanel.add(categoryLabel);

        String[] categories = {"General", "OBC", "SC", "ST", "EWS"};
        categoryBox = UITheme.createComboBox(categories);
        categoryBox.setBounds(fieldX, 215, fieldWidth, fieldHeight);
        bodyPanel.add(categoryBox);

        // 5. Family Annual Income
        incomeLabel = new JLabel("Annual Family Income (in ₹ - Numbers only):");
        incomeLabel.setFont(UITheme.FONT_LABEL);
        incomeLabel.setForeground(UITheme.TEXT_DARK);
        incomeLabel.setBounds(labelX, 255, 400, 20);
        bodyPanel.add(incomeLabel);

        incomeField = UITheme.createTextField();
        incomeField.setBounds(fieldX, 275, fieldWidth, fieldHeight);
        bodyPanel.add(incomeField);

        // Submit & Back Buttons
        submitButton = UITheme.createSuccessButton("Submit Application");
        submitButton.setBounds(fieldX, 330, 240, 38);
        submitButton.addActionListener(this);
        bodyPanel.add(submitButton);

        backButton = UITheme.createSecondaryButton("Back to Dashboard");
        backButton.setBounds(290, 330, 155, 38);
        backButton.addActionListener(this);
        bodyPanel.add(backButton);

        add(bodyPanel, BorderLayout.CENTER);

        getRootPane().setDefaultButton(submitButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            handleSubmitApplication();
        } else if (e.getSource() == backButton) {
            new StudentDashboard(username, studentName);
            dispose();
        }
    }

    /**
     * Validates form inputs and inserts a new scholarship record into MySQL `applications` table.
     */
    private void handleSubmitApplication() {
        String name = nameField.getText().trim();
        String classYear = classField.getText().trim();
        String course = courseField.getText().trim();
        String category = (String) categoryBox.getSelectedItem();
        String incomeStr = incomeField.getText().trim();

        // 1. Mandatory Text Validations
        if (name.isEmpty() || classYear.isEmpty() || course.isEmpty() || incomeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all the required application fields.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Numeric Family Income Validation
        double familyIncome;
        try {
            familyIncome = Double.parseDouble(incomeStr);
            if (familyIncome <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Family income must be greater than zero.",
                        "Invalid Income",
                        JOptionPane.WARNING_MESSAGE);
                incomeField.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid numeric value for family income (e.g., 150000).",
                    "Invalid Income Format",
                    JOptionPane.WARNING_MESSAGE);
            incomeField.requestFocus();
            return;
        }

        // 3. Database Insertion
        String sql = "INSERT INTO applications (username, student_name, class_year, course_name, category, family_income, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, 'Pending')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, this.username);
            pstmt.setString(2, name);
            pstmt.setString(3, classYear);
            pstmt.setString(4, course);
            pstmt.setString(5, category);
            pstmt.setDouble(6, familyIncome);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                int generatedId = -1;
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                    }
                }

                String successMsg = "Your scholarship application has been submitted successfully!\n\n"
                        + "Application ID : #" + (generatedId > 0 ? generatedId : "N/A") + "\n"
                        + "Student Name  : " + name + "\n"
                        + "Course        : " + course + "\n"
                        + "Initial Status: PENDING VERIFICATION\n\n"
                        + "You can track the progress in the 'View Application Status' section.";

                JOptionPane.showMessageDialog(this,
                        successMsg,
                        "Application Submitted",
                        JOptionPane.INFORMATION_MESSAGE);

                new StudentDashboard(username, name);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Unable to submit application. Please try again.",
                        "Submission Failed",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error while submitting application:\n" + ex.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

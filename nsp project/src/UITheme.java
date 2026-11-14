/*
 * UI Design System and Theme Helper
 * Project: National Scholarship Portal (NSP)
 *
 * Provides a modern, cohesive UI theme with custom styling,
 * typography, button palettes, and JTable enhancements.
 */

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UITheme {

    // Color Palette
    public static final Color PRIMARY        = new Color(24, 43, 73);       // Navy Blue
    public static final Color PRIMARY_HOVER  = new Color(38, 68, 115);
    public static final Color ACCENT         = new Color(37, 99, 235);      // Royal Blue
    public static final Color ACCENT_HOVER   = new Color(29, 78, 216);
    public static final Color SUCCESS        = new Color(22, 163, 74);      // Emerald Green
    public static final Color SUCCESS_HOVER  = new Color(21, 128, 61);
    public static final Color DANGER         = new Color(220, 38, 38);      // Crimson Red
    public static final Color DANGER_HOVER   = new Color(185, 28, 28);
    public static final Color WARNING        = new Color(217, 119, 6);      // Amber
    public static final Color BG_LIGHT       = new Color(248, 250, 252);    // Clean Slate
    public static final Color CARD_BG        = Color.WHITE;
    public static final Color BORDER_COLOR   = new Color(203, 213, 225);
    public static final Color TEXT_DARK      = new Color(15, 23, 42);
    public static final Color TEXT_MUTED     = new Color(100, 116, 139);
    public static final Color TEXT_LIGHT     = Color.WHITE;

    // Badges Backgrounds
    public static final Color BADGE_PENDING_BG  = new Color(254, 243, 199);
    public static final Color BADGE_PENDING_FG  = new Color(180, 83, 9);
    public static final Color BADGE_APPROVED_BG = new Color(220, 252, 231);
    public static final Color BADGE_APPROVED_FG = new Color(21, 128, 61);
    public static final Color BADGE_REJECTED_BG = new Color(254, 226, 226);
    public static final Color BADGE_REJECTED_FG = new Color(185, 28, 28);

    // Typography
    public static final Font FONT_TITLE        = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBTITLE     = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_HEADER       = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_LABEL        = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_INPUT        = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BUTTON       = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TABLE_CELL   = new Font("Segoe UI", Font.PLAIN, 12);

    /**
     * Applies system look and feel and antialiasing hints.
     */
    public static void applySystemLookAndFeel() {
        try {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    /**
     * Creates a styled modern button with mouse hover animation.
     */
    public static JButton createStyledButton(String text, Color baseColor, Color hoverColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setForeground(textColor);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(baseColor, 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(baseColor);
                }
            }
        });

        return button;
    }

    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, ACCENT, ACCENT_HOVER, Color.WHITE);
    }

    public static JButton createDarkButton(String text) {
        return createStyledButton(text, PRIMARY, PRIMARY_HOVER, Color.WHITE);
    }

    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, SUCCESS, SUCCESS_HOVER, Color.WHITE);
    }

    public static JButton createDangerButton(String text) {
        return createStyledButton(text, DANGER, DANGER_HOVER, Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setForeground(TEXT_DARK);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(241, 245, 249));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    /**
     * Creates a styled text field with comfortable padding and subtle border.
     */
    public static JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_DARK);
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return field;
    }

    /**
     * Creates a styled password field.
     */
    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_DARK);
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return field;
    }

    /**
     * Creates a styled combo box.
     */
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(FONT_INPUT);
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_DARK);
        return combo;
    }

    /**
     * Generates a modern header panel with portal title, subtitle, and user badge.
     */
    public static JPanel createHeaderPanel(String title, String subtitle, String userBadge) {
        JPanel header = new JPanel(new BorderLayout(10, 5));
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        leftPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_TITLE);
        titleLbl.setForeground(Color.WHITE);
        leftPanel.add(titleLbl);

        JLabel subLbl = new JLabel(subtitle != null ? subtitle : "Government of India | Scholarship Management System");
        subLbl.setFont(FONT_SUBTITLE);
        subLbl.setForeground(new Color(203, 213, 225));
        leftPanel.add(subLbl);

        header.add(leftPanel, BorderLayout.CENTER);

        if (userBadge != null && !userBadge.trim().isEmpty()) {
            JLabel badgeLbl = new JLabel(" " + userBadge + " ");
            badgeLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            badgeLbl.setForeground(Color.WHITE);
            badgeLbl.setBackground(new Color(38, 68, 115));
            badgeLbl.setOpaque(true);
            badgeLbl.setBorder(new CompoundBorder(
                    new LineBorder(new Color(71, 85, 105), 1, true),
                    new EmptyBorder(4, 10, 4, 10)
            ));
            JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
            badgeWrapper.setOpaque(false);
            badgeWrapper.add(badgeLbl);
            header.add(badgeWrapper, BorderLayout.EAST);
        }

        return header;
    }

    /**
     * Applies modern table styling: custom header, row height, alternate row colors,
     * and status badge renderer for the "Status" column.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_TABLE_CELL);
        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_DARK);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_HEADER);
        header.setBackground(PRIMARY);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        // Default Cell Renderer with Zebra striping & Status Badge
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                label.setBorder(new EmptyBorder(0, 10, 0, 10));

                String colName = tbl.getColumnName(column);

                if ("Status".equalsIgnoreCase(colName)) {
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    String statusText = value != null ? value.toString().trim() : "";
                    if ("Approved".equalsIgnoreCase(statusText)) {
                        label.setBackground(isSelected ? tbl.getSelectionBackground() : BADGE_APPROVED_BG);
                        label.setForeground(BADGE_APPROVED_FG);
                    } else if ("Rejected".equalsIgnoreCase(statusText)) {
                        label.setBackground(isSelected ? tbl.getSelectionBackground() : BADGE_REJECTED_BG);
                        label.setForeground(BADGE_REJECTED_FG);
                    } else { // Pending
                        label.setBackground(isSelected ? tbl.getSelectionBackground() : BADGE_PENDING_BG);
                        label.setForeground(BADGE_PENDING_FG);
                    }
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    if ("App ID".equalsIgnoreCase(colName) || "Application ID".equalsIgnoreCase(colName) || "Category".equalsIgnoreCase(colName)) {
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                    } else if ("Income (₹)".equalsIgnoreCase(colName) || "Family Income".equalsIgnoreCase(colName)) {
                        label.setHorizontalAlignment(SwingConstants.RIGHT);
                    } else {
                        label.setHorizontalAlignment(SwingConstants.LEFT);
                    }

                    label.setFont(FONT_TABLE_CELL);
                    if (isSelected) {
                        label.setBackground(tbl.getSelectionBackground());
                        label.setForeground(tbl.getSelectionForeground());
                    } else {
                        // Zebra Striping
                        label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                        label.setForeground(TEXT_DARK);
                    }
                }
                return label;
            }
        });
    }
}

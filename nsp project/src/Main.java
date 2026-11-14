/*
 * Project   : National Scholarship Portal (NSP)
 * Made by   : Kapil Tamoli
 * Upgraded  : End-to-End MySQL JDBC Integration & Modern UI
 *
 * This is the application entry point.
 * Run this file to launch the National Scholarship Portal.
 */

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Initialize modern Look & Feel and Swing font anti-aliasing hints
        UITheme.applySystemLookAndFeel();

        // Safely start the Swing GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new Login();
        });
    }
}

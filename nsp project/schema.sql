-- =======================================================================
-- National Scholarship Portal (NSP) - Database Schema & Initial Data
-- Database Name : nsp_db
-- Description   : Tables for User Authentication and Scholarship Applications
-- Compatible with: MySQL 5.7+, MySQL 8.x, MariaDB (XAMPP / Workbench)
-- =======================================================================

-- 1. Create and select the database
CREATE DATABASE IF NOT EXISTS nsp_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE nsp_db;

-- 2. Drop existing tables if re-importing (ordered by foreign key dependency)
DROP TABLE IF EXISTS applications;
DROP TABLE IF EXISTS users;

-- =======================================================================
-- 3. Users Table (Stores Students and Administrators)
-- =======================================================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'student', -- 'student' or 'admin'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =======================================================================
-- 4. Applications Table (Stores Scholarship Applications)
-- =======================================================================
CREATE TABLE applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    class_year VARCHAR(50) NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    category VARCHAR(30) NOT NULL,
    family_income DECIMAL(12, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Pending', -- 'Pending', 'Approved', 'Rejected'
    applied_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_app_user FOREIGN KEY (username)
        REFERENCES users (username)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create indexes for fast lookup on frequent queries
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_applications_username ON applications(username);
CREATE INDEX idx_applications_status ON applications(status);

-- =======================================================================
-- 5. Seed Data: Default Admin & Demo Accounts
-- =======================================================================

-- Default Administrator Account
-- Login: username = 'admin', password = 'admin123'
INSERT INTO users (full_name, email, phone, username, password, role)
VALUES ('System Administrator', 'admin@nsp.gov.in', '9876543210', 'admin', 'admin123', 'admin');

-- Demo Student Accounts
INSERT INTO users (full_name, email, phone, username, password, role) VALUES
('Kapil Tamoli', 'kapil@example.com', '9876543211', 'kapil', 'kapil123', 'student'),
('Riya Sharma', 'riya@example.com', '9876543212', 'riya', 'riya123', 'student'),
('Aman Verma', 'aman@example.com', '9876543213', 'aman', 'aman123', 'student');

-- =======================================================================
-- 6. Seed Data: Sample Scholarship Applications
-- =======================================================================
INSERT INTO applications (username, student_name, class_year, course_name, category, family_income, status, applied_date) VALUES
('kapil', 'Kapil Tamoli', '3rd Year', 'B.Tech CSE', 'General', 180000.00, 'Pending', NOW() - INTERVAL 2 DAY),
('riya',  'Riya Sharma',  '2nd Year', 'B.Sc Physics', 'OBC',     120000.00, 'Approved', NOW() - INTERVAL 5 DAY),
('aman',  'Aman Verma',   '1st Year', 'BCA',          'EWS',      95000.00, 'Rejected', NOW() - INTERVAL 7 DAY);

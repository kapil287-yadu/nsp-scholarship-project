NSP PROJECT (National Scholarship Portal)
Made by: Kapil Tamoli
Tools: IntelliJ IDEA, Java (Swing), MySQL (to be connected later)

===========================================
HOW TO OPEN IN INTELLIJ IDEA
===========================================
1. Open IntelliJ IDEA -> File -> Open -> select this "nsp project" folder.
2. If asked, mark the "src" folder as "Sources Root"
   (right-click on src folder -> Mark Directory as -> Sources Root).
3. Open Main.java and click the green Run button.
4. The Login page will open first.

===========================================
PAGES / FILES IN THIS PROJECT
===========================================
1. Main.java              -> starting point, opens the Login page
2. Login.java              -> student & admin login page
3. Register.java           -> new student registration page
4. StudentDashboard.java   -> home page after student logs in
5. ApplyScholarship.java   -> form to apply for a scholarship
6. ViewStatus.java         -> student can see their application status
7. AdminDashboard.java     -> home page after admin logs in
8. ViewApplications.java   -> admin can see all student applications

===========================================
NOTES FOR VIVA
===========================================
- This is only the FRONT-END (GUI pages) of the project, made using
  Java Swing (JFrame, JLabel, JTextField, JButton, JTable, etc.)
- No MySQL connection code is included yet — every place where the
  database will be used later is marked with a comment starting
  with "// TODO:".
- Right now, Login/Register/Apply buttons just move to the next page
  or show a message box — they don't check/save real data.
- Tables in ViewStatus.java and ViewApplications.java currently show
  SAMPLE data typed directly in the code, so the pages look complete
  even without a database.
- Every class is simple on purpose: one JFrame per page, buttons
  with actionListener, and setBounds() for placing components —
  easy to read and explain line by line.

===========================================
NEXT STEP (NOT DONE YET)
===========================================
- Add MySQL Connector/J jar to the project.
- Write a DBConnection.java class with connection details.
- Replace the "// TODO:" comments with real SQL queries
  (INSERT for Login/Register/Apply, SELECT for ViewStatus/ViewApplications).

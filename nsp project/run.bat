@echo off
REM ====================================================================
REM National Scholarship Portal (NSP) - Quick Run Script
REM ====================================================================

echo [NSP] Compiling Java source files...
if not exist "bin" mkdir bin

REM Check if javac is in PATH, otherwise fallback to IntelliJ JBR
where javac >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    set JAVAC_BIN=javac
    set JAVA_BIN=java
) else (
    set JAVAC_BIN="C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.2\jbr\bin\javac.exe"
    set JAVA_BIN="C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.2\jbr\bin\java.exe"
)

%JAVAC_BIN% -d bin src\*.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed. Please inspect errors above.
    pause
    exit /b %ERRORLEVEL%
)

echo [NSP] Starting National Scholarship Portal...
if exist "lib\mysql-connector-j-8.4.0.jar" (
    %JAVA_BIN% -cp "bin;lib\mysql-connector-j-8.4.0.jar" Main
) else if exist "lib\*.jar" (
    %JAVA_BIN% -cp "bin;lib\*" Main
) else (
    %JAVA_BIN% -cp bin Main
)

pause
